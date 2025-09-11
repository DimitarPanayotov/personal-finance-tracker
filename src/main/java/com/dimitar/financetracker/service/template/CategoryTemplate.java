package com.dimitar.financetracker.service.template;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryTemplate {
    private String name;
    private CategoryType type;
    private String color;

    public Category toCategory(User user) {
        return Category.builder()
                .name(this.name)
                .type(this.type)
                .color(this.color)
                .user(user)
                .build();
    }
}
