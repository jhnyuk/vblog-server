package com.example.vblogserver.domain.category.initializer;

import com.example.vblogserver.domain.category.entity.CategoryS;
import com.example.vblogserver.domain.category.repository.CategorySRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategorySInitializer implements CommandLineRunner {
    private final CategorySRepository categorySRepository;

    public CategorySInitializer(CategorySRepository categorySRepository) {
        this.categorySRepository = categorySRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        CategoryS categoryS1= new CategoryS();
        categoryS1.setCategoryName("국내여행");
        categorySRepository.save(categoryS1);

        CategoryS categoryS2= new CategoryS();
        categoryS2.setCategoryName("해외여행");
        categorySRepository.save(categoryS2);
    }
}
