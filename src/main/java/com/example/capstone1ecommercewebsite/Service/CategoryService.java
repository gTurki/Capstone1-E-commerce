package com.example.capstone1ecommercewebsite.Service;

import com.example.capstone1ecommercewebsite.Model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryService {

    private final ArrayList<Category> categories = new ArrayList<>();

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public boolean updateCategory(String id, Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equalsIgnoreCase(id)) {
                categories.set(i, category);
                return true;
            }
        }
        return false;
    }

    public boolean deleteCategory(String id) {
        for (Category c : categories) {
            if (c.getId().equalsIgnoreCase(id)) {
                categories.remove(c);
                return true;
            }
        }
        return false;
    }
}
