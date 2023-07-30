package com.example.vblogserver.domain.category.initializer;

import com.example.vblogserver.domain.category.entity.CategoryG;
import com.example.vblogserver.domain.category.repository.CategoryGRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryGInitializer implements CommandLineRunner {
    private final CategoryGRepository categoryGRepository;

    @Autowired
    public CategoryGInitializer(CategoryGRepository categoryGRepository){
        this.categoryGRepository = categoryGRepository;
    }

    @Override
    public void run(String... arg){
        CategoryG categoryG1 = new CategoryG();
        categoryG1.setCategoryName("vlog");
        categoryGRepository.save(categoryG1);

        CategoryG categoryG2 = new CategoryG();
        categoryG2.setCategoryName("blog");
        categoryGRepository.save(categoryG2);
    }
}
