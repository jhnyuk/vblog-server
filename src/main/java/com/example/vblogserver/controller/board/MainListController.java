package com.example.vblogserver.controller.board;

import com.example.vblogserver.domain.board.entity.Board;
import com.example.vblogserver.domain.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vlog")
public class MainListController {

    private final BoardRepository boardRepository;

    @Autowired
    public MainListController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @GetMapping("/list")
    public List<MainBoardDTO> getClientData() {
        List<Board> boards = boardRepository.findAll();
        int limit = 20;
        List<MainBoardDTO> clientDataDTOs = boards.stream()
                .limit(limit)
                .map(this::convertToClientDataDTO)
                .collect(Collectors.toList());

        return clientDataDTOs;
    }

    // Board 엔티티를 원하는 형태의 DTO로 변환
    private MainBoardDTO convertToClientDataDTO(Board board) {
        MainBoardDTO clientDataDTO = new MainBoardDTO();
        clientDataDTO.setContentDate(board.getCreatedDate());
        clientDataDTO.setContentTitle(board.getTitle());
        clientDataDTO.setUserName(board.getWriter());
        clientDataDTO.setContent(board.getDescription());
        clientDataDTO.setHashtags(board.getHashtag());
        clientDataDTO.setContentId(board.getId());
        //imageUrl

        return clientDataDTO;
    }
}