//package com.example.caloryxbackend.macros;
//
//import com.example.caloryxbackend.macros.payload.MacrosResponse;
//import com.example.caloryxbackend.user.User;
//import org.springframework.stereotype.Service;
//
//@Service
//public class MacrosService {
//    public MacrosResponse calculateMacros(User user) {
//        double carbsCalories = calories * (carbsPercentage / 100);
//        double proteinCalories = calories * (proteinPercentage / 100);
//        double fatCalories = calories * (fatPercentage / 100);
//
//        double carbsGrams = carbsCalories / 4; // 1 gram of carbs = 4 calories
//        double proteinGrams = proteinCalories / 4; // 1 gram of protein = 4 calories
//        double fatGrams = fatCalories / 9; // 1 gram of fat = 9 calories
//
//        return new MacrosResponse(carbsGrams, proteinGrams, fatGrams);
//    }
//}
