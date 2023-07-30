package com.example.vblogserver.domain.category.initializer;

import com.example.vblogserver.domain.category.entity.CategoryM;
import com.example.vblogserver.domain.category.repository.CategoryMRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryMInitializer implements CommandLineRunner {
    private final CategoryMRepository categoryMRepository;

    public CategoryMInitializer(CategoryMRepository categoryMRepository) {
        this.categoryMRepository = categoryMRepository;
    }

    @Override
    public void run(String... args){
        CategoryM categoryM1 = new CategoryM();
        categoryM1.setCategoryName("여행");
        categoryMRepository.save(categoryM1);
    }
}
