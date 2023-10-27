package com.example.vblogserver.domain.board.controller.main;

import com.example.vblogserver.domain.board.dto.MainBoardDTO;
import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import com.example.vblogserver.domain.board.service.BoardService;
import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.click.entity.Click;
import com.example.vblogserver.domain.click.repository.ClickRepository;
import com.example.vblogserver.domain.user.dto.PageResponseDto;
import com.example.vblogserver.domain.user.entity.User;
import com.example.vblogserver.domain.user.entity.UserOption;
import com.example.vblogserver.domain.user.repository.UserOptionRepository;
import com.example.vblogserver.domain.user.repository.UserRepository;
import com.example.vblogserver.global.jwt.service.JwtService;
import com.example.vblogserver.global.jwt.util.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.vblogserver.domain.board.dto.CategoryMDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ClickBasedController {
    private final ClickRepository clickRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserOptionRepository userOptionRepository;


    public ClickBasedController(ClickRepository clickRepository, BoardRepository boardRepository, BoardService boardService, JwtService jwtService, UserRepository userRepository, UserOptionRepository userOptionRepository) {
        this.clickRepository = clickRepository;
        this.boardRepository = boardRepository;
        this.boardService = boardService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userOptionRepository = userOptionRepository;
    }

    public List<Click> getClicksByUserId(User user) {
        return clickRepository.findByUser(user);
    }

    //사용자 맞춤 추천 게시글 조회 (게시글 클릭 정보 + 회원가입 시 입력받은 카테고리를 기반으로 추천 게시글 조회)
    @GetMapping("/vlog/userBase")
    public List<MainBoardDTO> getUserBasedVlogList(HttpServletRequest request) {
    //public String getUserBasedVlogList(HttpServletRequest request) {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(1L);
        return getUserBasedBoards(request, categoryG);

    }

    @GetMapping("/blog/userBase")
    public List<MainBoardDTO> getUserBasedBlogList(HttpServletRequest request) {
        CategoryG categoryG = new CategoryG();
        categoryG.setId(2L);
        return getUserBasedBoards(request, categoryG);
    }

    //사용자가 클릭한 게시글의 카테고리 중 가장 많이 조회된 게시글의 카테고리 TOP2 조회
    public List<MainBoardDTO> getUserBasedBoards(HttpServletRequest request, CategoryG categoryG){
    //public String getUserBasedBoards(HttpServletRequest request, CategoryG categoryG){
        // 액세스 토큰 추출
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

        // 액세스 토큰이 존재하지 않거나 유효하지 않다면 에러 응답 반환
        if (accessTokenOpt.isEmpty() || !jwtService.isTokenValid(accessTokenOpt.get())) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 액세스 토큰에서 로그인 아이디 추출
        Optional<String> loginIdOpt = jwtService.extractId(accessTokenOpt.get());

        // 로그인 아이디가 존재하지 않으면 에러 응답 반환
        if (loginIdOpt.isEmpty()) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userId = loginIdOpt.get();

        User owner = userRepository.findByLoginId(userId)
                .orElseThrow(() -> new NotFoundException(userId + "을 찾을 수 없습니다"));

        // 특정 사용자가 클릭한 게시글 리스트 조회
        List<Click> clicksByUser = clickRepository.findByUser(owner);

        // 각 게시글의 categoryM을 count하기 위한 Map을 생성
        Map<CategoryM, Long> categoryCountMap = clicksByUser.stream()
                .map(Click::getBoard)
                .map(Board::getCategoryM)
                .collect(Collectors.groupingBy(categoryM -> categoryM, Collectors.counting()));

        // CategoryM을 count 하여 내림차순으로 정렬
        List<Map.Entry<CategoryM, Long>> sortedCategories = categoryCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .collect(Collectors.toList());


        // userCategories 리스트와 signupCategory 리스트를 합칠 리스트를 생성
        List<String> combinedCategories = new ArrayList<>();

        // 상위 2개의 카테고리 조회
        List<CategoryMDTO> userCategories;
        // 조회된 카테고리가 2개 이상인 경우
        if (sortedCategories.size() >= 2) {
            // 상위 2개의 카테고리를 선택합니다.
            userCategories = sortedCategories.stream()
                    .limit(2)
                    .map(entry -> new CategoryMDTO(entry.getKey().getCategoryName()))
                    .collect(Collectors.toList());
            // userCategories 리스트가 null이 아니라면 합칠 리스트에 추가
            if (userCategories != null) {
                for(CategoryMDTO categoryMDTO : userCategories)
                combinedCategories.add(categoryMDTO.getCategoryName());
            }
        }

        // 회원가입 시 사용자가 선택한 카테고리 조회
        List<UserOption> signupCategory = userOptionRepository.findByUserId(owner.getId());
        // 클릭한 게시글의 카테고리에서 사용자가 선택한 카테고리도 추가
        for(UserOption userOption : signupCategory){
            combinedCategories.add(String.valueOf(userOption.getOption().getType()));
        }

        // 중복 카테고리 제거
        List<String> uniqueCategoriesList = combinedCategories.stream()
                .distinct()
                .collect(Collectors.toList());

        List<Board> userBasedBoards = new ArrayList<>();

        for(String uniquecategory : uniqueCategoriesList){
            CategoryM categoryM = new CategoryM();
            categoryM.setId(covCategory(uniquecategory));
            userBasedBoards = boardRepository.findByCategoryGAndCategoryM(categoryG, categoryM);

        }

        int limit = 20;
        List<MainBoardDTO> clientDataDTOs = userBasedBoards.stream()
                .limit(limit)
                .sorted((a, b) -> Math.random() < 0.5 ? -1 : 1) // 랜덤
                .map(this::convertToClientDataDTO)
                .collect(Collectors.toList());


        return clientDataDTOs;
    }

    private Long covCategory(String CategoryName){
        Long covCategory=1L;
        switch (CategoryName) {
            case "여행":
                covCategory = 1L;
                break;
            case "게임":
                covCategory = 2L;
                break;
            case "건강":
                covCategory = 3L;
                break;
            case "맛집":
                covCategory = 4L;
                break;
            case "방송":
                covCategory = 5L;
                break;
            case "뷰티":
                covCategory = 6L;
                break;
        }
        return covCategory;
    }

    private MainBoardDTO convertToClientDataDTO(Board board) {
        MainBoardDTO clientDataDTO = new MainBoardDTO();
        LocalDate ContentDate = board.getCreatedDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDateTime = ContentDate.format(formatter);
        clientDataDTO.setContentDate(formattedDateTime);
        clientDataDTO.setContentTitle(board.getTitle());
        clientDataDTO.setUserName(board.getWriter());
        clientDataDTO.setContent(board.getDescription());
        clientDataDTO.setHashtags(board.getHashtag());
        clientDataDTO.setContentId(board.getId());
        clientDataDTO.setImgurl(board.getThumbnails());
        clientDataDTO.setHeart(board.getLikeCount());
        if (board.getReviewCount() != null) {
            clientDataDTO.setReview(board.getReviewCount());
        }


        return clientDataDTO;
    }
}
