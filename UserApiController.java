package com.health.controller;

import com.health.model.*;
import com.health.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @Autowired private UserRepository userRepository;
    @Autowired private DietPlanRepository dietPlanRepository;
    @Autowired private WorkoutPlanRepository workoutPlanRepository;
    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private SetupQuestionRepository setupQuestionRepository;
    @Autowired private UserProgressRepository userProgressRepository;

    private static final String ADMIN_PASSWORD = "admin123";

    @PostConstruct
    public void seedData() {
        Optional<User> existing = userRepository.findByEmail("admin@gmail.com");
        if (existing.isEmpty()) {
            User admin = new User("Admin", "admin@gmail.com", ADMIN_PASSWORD, "0000000000", "admin");
            userRepository.save(admin);
        } else {
            User admin = existing.get();
            admin.setRole("admin");
            admin.setDisplayPwd(ADMIN_PASSWORD);
            userRepository.save(admin);
        }

        // Re-seed diet plans if less than 20 exist or any plan is missing its weekly schedule
        {
            boolean needsReseed = dietPlanRepository.count() < 20 ||
                dietPlanRepository.findAll().stream()
                    .anyMatch(p -> p.getWeeklySchedule() == null || p.getWeeklySchedule().isEmpty());
            if (needsReseed) {
                dietPlanRepository.deleteAll();
                String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
                seedAllDietPlans(today);
            }
        }
        if (workoutPlanRepository.count() == 0) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            seedWorkout("4-Week Fat Burn", "Lose Weight", "Beginner", "4 Weeks", "3-4 days",
                "High intensity cardio and bodyweight exercises for fat loss. Burn calories fast with HIIT circuits.",
                "/videos/video.mp4", today);
            seedWorkout("Muscle Builder Pro", "Build Muscle", "Intermediate", "8 Weeks", "4-5 days",
                "Progressive overload strength training for muscle hypertrophy. Build lean mass effectively.",
                "/videos/Video2.mp4", today);
            seedWorkout("Stay Fit Routine", "Stay Fit", "Beginner", "4 Weeks", "3 days",
                "Balanced mix of cardio and strength for overall fitness. Perfect for maintaining health.",
                "/videos/video3.mp4", today);
            seedWorkout("Flexibility & Mobility", "Flexibility", "Beginner", "4 Weeks", "5 days",
                "Daily stretching and yoga-inspired mobility work. Improve range of motion and reduce injury risk.",
                "/videos/video4.mp4", today);
            seedWorkout("Advanced Shred", "Lose Weight", "Advanced", "6 Weeks", "5-6 days",
                "Intense HIIT and strength combo for maximum fat burn. For experienced athletes only.",
                "/videos/video5.mp4", today);
            seedWorkout("Beginner Strength", "Build Muscle", "Beginner", "6 Weeks", "3 days",
                "Full body compound movements for beginners. Learn proper form and build a solid foundation.",
                "/videos/videos6.mp4", today);
            seedWorkout("Core Power", "Stay Fit", "Intermediate", "4 Weeks", "4 days",
                "Focused core and stability training. Strengthen your midsection for better posture and performance.",
                "/videos/videos7.mp4", today);
            seedWorkout("HIIT Cardio Blast", "Lose Weight", "Intermediate", "4 Weeks", "4-5 days",
                "High-intensity interval training to torch calories and boost metabolism all day long.",
                "/videos/videos8.mp4", today);
            seedWorkout("Yoga & Stretch", "Flexibility", "Beginner", "6 Weeks", "5 days",
                "Gentle yoga flows and deep stretching for flexibility, stress relief and mental clarity.",
                "/videos/videos9.mp4", today);
            seedWorkout("Power Athlete", "Build Muscle", "Advanced", "8 Weeks", "5-6 days",
                "Advanced powerlifting and athletic conditioning for serious muscle and strength gains.",
                "/videos/videos10.mp4", today);
        } else {
            // Patch existing plans that have no videoUrl
            String[] videoFiles = {"/videos/video.mp4","/videos/Video2.mp4","/videos/video3.mp4",
                "/videos/video4.mp4","/videos/video5.mp4","/videos/videos6.mp4",
                "/videos/videos7.mp4","/videos/videos8.mp4","/videos/videos9.mp4","/videos/videos10.mp4"};
            java.util.List<WorkoutPlan> allPlans = workoutPlanRepository.findAll();
            for (int i = 0; i < allPlans.size(); i++) {
                WorkoutPlan wp = allPlans.get(i);
                if (wp.getVideoUrl() == null || wp.getVideoUrl().isEmpty()) {
                    wp.setVideoUrl(videoFiles[i % videoFiles.length]);
                    workoutPlanRepository.save(wp);
                }
            }
        }

        if (announcementRepository.count() == 0) {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            seedAnnouncement("Welcome to Elite Fitness!", "Start your fitness journey today. Complete your setup to get a personalized plan.", "Success", today);
            seedAnnouncement("New Diet Plans Added", "Check out our latest nutrition plans tailored for weight loss, muscle gain and more.", "Info", today);
            seedAnnouncement("Stay Consistent", "Consistency is key! Log your workouts daily and track your progress.", "Info", today);
        }

        if (setupQuestionRepository.count() == 0) {
            seedQ(1, "What is your main goal?", "Lose Weight,Build Muscle,Stay Fit,Improve Flexibility");
            seedQ(2, "Your current activity level?", "Beginner,Lightly Active,Moderately Active,Very Active");
            seedQ(3, "What is your diet preference?", "Vegetarian,Non-Vegetarian,Vegan,No Preference");
            seedQ(4, "Your workout experience?", "New to workout,Some experience,Regular workout");
            seedQ(5, "Do you have any health condition?", "No,Diabetes,Thyroid,PCOS / Hormonal");
            seedQ(6, "How many days per week can you workout?", "2 â€“ 3 days,3 â€“ 4 days,5 days,6 days");
        }
    }

    private void seedQ(int order, String question, String options) {
        SetupQuestion q = new SetupQuestion();
        q.setQuestion(question); q.setOptions(options);
        q.setQuestionOrder(order); q.setStatus("Active");
        setupQuestionRepository.save(q);
    }

    private void seedAnnouncement(String title, String message, String type, String date) {
        Announcement a = new Announcement();
        a.setTitle(title); a.setMessage(message);
        a.setType(type); a.setStatus("Active"); a.setCreatedDate(date);
        announcementRepository.save(a);
    }

    private void seedWorkout(String title, String goal, String level, String duration,
                              String days, String desc, String videoUrl, String created) {
        WorkoutPlan p = new WorkoutPlan();
        p.setTitle(title); p.setGoal(goal); p.setLevel(level);
        p.setDuration(duration); p.setDaysPerWeek(days);
        p.setDescription(desc); p.setVideoUrl(videoUrl);
        p.setStatus("Active"); p.setCreated(created);
        workoutPlanRepository.save(p);
    }

    private String buildSchedule(
        String monB, String monL, String monD, String monS,
        String tueB, String tueL, String tueD, String tueS,
        String wedB, String wedL, String wedD, String wedS,
        String thuB, String thuL, String thuD, String thuS,
        String friB, String friL, String friD, String friS,
        String satB, String satL, String satD, String satS,
        String sunB, String sunL, String sunD, String sunS) {
        return String.format(
            "{\"Mon\":{\"breakfast\":\"%s\",\"lunch\":\"%s\",\"dinner\":\"%s\",\"snacks\":\"%s\"}," +
            "\"Tue\":{\"breakfast\":\"%s\",\"lunch\":\"%s\",\"dinner\":\"%s\",\"snacks\":\"%s\"}," +
            "\"Wed\":{\"breakfast\":\"%s\",\"lunch\":\"%s\",\"dinner\":\"%s\",\"snacks\":\"%s\"}," +
            "\"Thu\":{\"breakfast\":\"%s\",\"lunch\":\"%s\",\"dinner\":\"%s\",\"snacks\":\"%s\"}," +
            "\"Fri\":{\"breakfast\":\"%s\",\"lunch\":\"%s\",\"dinner\":\"%s\",\"snacks\":\"%s\"}," +
            "\"Sat\":{\"breakfast\":\"%s\",\"lunch\":\"%s\",\"dinner\":\"%s\",\"snacks\":\"%s\"}," +
            "\"Sun\":{\"breakfast\":\"%s\",\"lunch\":\"%s\",\"dinner\":\"%s\",\"snacks\":\"%s\"}}",
            monB,monL,monD,monS, tueB,tueL,tueD,tueS, wedB,wedL,wedD,wedS,
            thuB,thuL,thuD,thuS, friB,friL,friD,friS, satB,satL,satD,satS,
            sunB,sunL,sunD,sunS);
    }

    private void seedAllDietPlans(String today) {
        // ── LOSE WEIGHT ──────────────────────────────────────────────────────
        // Lose Weight + Non-Vegetarian + No Condition
        seedDiet("Weight Loss – Non-Veg", "Fitness", "Fat Loss", "4 Weeks", "Lose Weight", "Non-Vegetarian", "No",
            "High-protein calorie-deficit plan with lean meats and complex carbs to burn fat fast.",
            buildSchedule("Oats+boiled eggs+green tea","Grilled chicken+brown rice+salad","Steamed fish+veggies+soup","Apple+almonds",
                "Egg white omelette+toast","Tuna salad+roti","Baked chicken+broccoli","Cucumber+buttermilk",
                "Poha+green tea","Chicken soup+salad","Grilled prawns+veggies","Mixed nuts",
                "Oats+banana","Grilled fish+quinoa","Chicken stir fry+veggies","Protein shake",
                "Boiled eggs+toast","Chicken breast+salad","Baked fish+sweet potato","Fruit salad",
                "Smoothie bowl","Grilled chicken+brown rice","Steamed fish+veggies","Roasted chickpeas",
                "Light fruits","Veg soup","Grilled chicken+salad","Yogurt"), today);
        // Lose Weight + Vegetarian + No Condition
        seedDiet("Weight Loss – Veg", "Nutrition", "Fat Loss", "4 Weeks", "Lose Weight", "Vegetarian", "No",
            "Plant-based calorie-deficit plan rich in fiber and protein for sustainable fat loss.",
            buildSchedule("Oats+skimmed milk+banana","Paneer salad+brown rice","Dal+sabzi+roti","Apple",
                "Poha+green tea","Rajma+brown rice","Palak paneer+roti","Roasted makhana",
                "Idli+sambar","Chole+roti","Mixed veg+dal+rice","Buttermilk",
                "Upma+green tea","Paneer bhurji+roti","Moong dal+sabzi","Fruit bowl",
                "Smoothie+nuts","Sprouts salad+roti","Tofu stir fry+rice","Yogurt",
                "Dalia+milk","Veg soup+roti","Dal khichdi+salad","Roasted chana",
                "Fruits only","Veg salad","Khichdi+curd","Coconut water"), today);
        // Lose Weight + Vegan + No Condition
        seedDiet("Weight Loss – Vegan", "Nutrition", "Fat Loss", "4 Weeks", "Lose Weight", "Vegan", "No",
            "100% plant-based fat loss plan with whole grains, legumes and seeds.",
            buildSchedule("Smoothie bowl+chia seeds","Quinoa+chickpea salad","Lentil soup+brown rice","Almonds+dates",
                "Oats+almond milk","Tofu stir fry+rice","Black bean curry+roti","Pumpkin seeds",
                "Avocado toast+green tea","Veg wrap+hummus","Dal+sabzi+rice","Fruit bowl",
                "Dalia+coconut milk","Sprouts salad+roti","Soya chunks+veggies","Walnuts",
                "Banana+flaxseeds","Chickpea salad+bread","Tofu curry+rice","Roasted makhana",
                "Smoothie+seeds","Veg soup+bread","Rajma+rice","Coconut water",
                "Raw fruits","Salad bowl","Khichdi (vegan)","Herbal tea+nuts"), today);
        // Lose Weight + No Preference + No Condition
        seedDiet("Weight Loss – General", "Fitness", "Fat Loss", "4 Weeks", "Lose Weight", "No Preference", "No",
            "Flexible calorie-deficit plan suitable for any diet preference.",
            buildSchedule("Oats+banana+green tea","Brown rice+dal+salad","Grilled protein+veggies","Apple+nuts",
                "Whole wheat toast+eggs/paneer","Soup+roti","Baked protein+broccoli","Buttermilk",
                "Poha+green tea","Salad+roti","Mixed veg+dal","Roasted makhana",
                "Upma+juice","Protein+roti","Dal khichdi+curd","Fruit bowl",
                "Smoothie","Veg biryani+raita","Grilled protein+veggies","Yogurt",
                "Dalia+milk","Rajma+rice","Sabzi+roti+dal","Mixed nuts",
                "Light meal","Veg soup","Khichdi+salad","Coconut water"), today);
        // Lose Weight + Diabetes
        seedDiet("Weight Loss – Diabetic", "Medical", "Fat Loss + Blood Sugar", "6 Weeks", "Lose Weight", "No Preference", "Diabetes",
            "Low glycemic, calorie-deficit plan to lose weight while managing blood sugar.",
            buildSchedule("Oats+skimmed milk (no sugar)","Brown rice+dal+salad","Grilled fish/paneer+veggies","Cucumber+nuts",
                "Methi paratha+curd","Multigrain roti+sabzi+dal","Baked chicken/tofu+salad","Buttermilk",
                "Poha (less oil)+green tea","Rajma+brown rice","Steamed protein+veggies","Apple slices",
                "Upma+green tea","Chole+roti+salad","Grilled protein+broccoli","Roasted makhana",
                "Egg white/paneer+toast","Veg soup+multigrain roti","Dal+sabzi+small rice","Yogurt (no sugar)",
                "Dalia+milk (no sugar)","Sprouts+roti","Baked protein+salad","Walnuts",
                "Low sugar fruits","Veg salad+soup","Khichdi+curd","Coconut water"), today);
        // Lose Weight + Thyroid
        seedDiet("Weight Loss – Thyroid", "Medical", "Fat Loss + Thyroid", "6 Weeks", "Lose Weight", "No Preference", "Thyroid",
            "Selenium-rich, low-calorie plan to support thyroid function while losing weight.",
            buildSchedule("Brazil nuts+oats+milk","Tuna/paneer salad+brown rice","Grilled fish+veggies","Pumpkin seeds",
                "Eggs/paneer+whole wheat toast","Chicken/tofu+quinoa+salad","Baked fish+sweet potato","Walnuts",
                "Smoothie+chia seeds","Seafood/paneer+roti","Dal+sabzi+rice","Sunflower seeds",
                "Oats+berries","Tuna wrap/veg wrap+salad","Grilled protein+broccoli","Almonds",
                "Egg omelette/paneer+toast","Chicken soup/veg soup+bread","Salmon/tofu+veggies","Fruit bowl",
                "Dalia+milk","Sprouts+roti","Fish curry/dal+rice","Roasted chana",
                "Selenium foods","Veg soup+bread","Khichdi+curd","Coconut water"), today);
        // Lose Weight + PCOS
        seedDiet("Weight Loss – PCOS", "Women Health", "Fat Loss + Hormone Balance", "6 Weeks", "Lose Weight", "Vegetarian", "PCOS / Hormonal",
            "Anti-inflammatory, low-calorie plan to manage PCOS and lose weight naturally.",
            buildSchedule("Flaxseed smoothie+nuts","Quinoa salad+paneer","Dal+sabzi+roti","Pumpkin seeds",
                "Oats+berries+milk","Chole+brown rice","Tofu stir fry+veggies","Walnuts",
                "Moong dal chilla+curd","Sprouts salad+roti","Palak paneer+roti","Sunflower seeds",
                "Dalia+milk","Rajma+rice+salad","Mixed veg+dal","Fruit bowl",
                "Smoothie bowl+chia seeds","Paneer bhurji+roti","Soya curry+rice","Almonds",
                "Idli+sambar","Veg soup+multigrain roti","Dal khichdi+curd","Roasted makhana",
                "Anti-inflammatory foods","Turmeric milk+salad","Khichdi+curd","Coconut water"), today);

        // ── BUILD MUSCLE ─────────────────────────────────────────────────────
        // Build Muscle + Non-Vegetarian + No Condition
        seedDiet("Muscle Gain – Non-Veg", "Fitness", "Muscle Building", "8 Weeks", "Build Muscle", "Non-Vegetarian", "No",
            "High-protein, high-calorie plan with lean meats for maximum muscle hypertrophy.",
            buildSchedule("6 eggs+oats+milk","Chicken breast+rice+salad","Beef/chicken+sweet potato+veggies","Protein shake+banana",
                "Omelette+whole wheat toast+juice","Tuna sandwich+salad","Grilled chicken+pasta","Peanut butter toast",
                "Pancakes+eggs+milk","Chicken rice bowl","Salmon+brown rice+broccoli","Nuts+protein bar",
                "Egg bhurji+paratha","Chicken curry+rice","Mutton/chicken+roti+dal","Banana shake",
                "Oats+protein powder+milk","Grilled fish+quinoa+salad","Chicken steak+veggies","Boiled eggs",
                "French toast+eggs","Chicken wrap+salad","Grilled chicken+mashed potato","Protein shake",
                "High protein rest","Chicken soup+bread","Baked chicken+rice","Milk+almonds"), today);
        // Build Muscle + Vegetarian + No Condition
        seedDiet("Muscle Gain – Veg", "Nutrition", "Muscle Building", "8 Weeks", "Build Muscle", "Vegetarian", "No",
            "Plant-based high-protein plan with paneer, legumes and dairy for muscle growth.",
            buildSchedule("Paneer paratha+milk+banana","Rajma rice+salad","Paneer curry+roti+dal","Protein shake+nuts",
                "Oats+milk+nuts","Chole+rice+salad","Tofu stir fry+roti","Peanut butter toast",
                "Moong dal chilla+curd","Paneer bhurji+roti","Dal makhani+rice","Banana+almonds",
                "Dalia+milk","Sprouts salad+roti","Soya chunks curry+rice","Protein bar",
                "Smoothie+paneer","Veg biryani+raita","Paneer tikka+roti","Milk+dates",
                "Idli+sambar+curd","Rajma+roti","Mixed dal+sabzi+rice","Roasted chana",
                "Fruits+milk","Veg soup+bread","Khichdi+curd","Nuts+milk"), today);
        // Build Muscle + Vegan + No Condition
        seedDiet("Muscle Gain – Vegan", "Nutrition", "Muscle Building", "8 Weeks", "Build Muscle", "Vegan", "No",
            "High-protein vegan plan using tofu, tempeh, legumes and seeds for muscle building.",
            buildSchedule("Smoothie+protein powder+banana","Tofu rice bowl+salad","Lentil curry+quinoa","Peanut butter+banana",
                "Oats+almond milk+nuts","Chickpea wrap+salad","Soya chunks curry+roti","Almonds+dates",
                "Dalia+coconut milk","Rajma+brown rice","Tofu stir fry+rice","Protein bar (vegan)",
                "Banana pancakes (vegan)","Chickpea salad+bread","Black bean curry+rice","Walnuts",
                "Smoothie+chia+flaxseeds","Veg biryani (vegan)","Tofu tikka+roti","Roasted chana",
                "Oats+almond milk","Sprouts+roti","Dal+sabzi+rice","Pumpkin seeds",
                "High protein fruits","Veg soup+bread","Khichdi (vegan)","Herbal tea+nuts"), today);
        // Build Muscle + No Preference + No Condition
        seedDiet("Muscle Gain – General", "Fitness", "Muscle Building", "8 Weeks", "Build Muscle", "No Preference", "No",
            "Flexible high-protein plan for muscle building suitable for any diet.",
            buildSchedule("Eggs/paneer+oats+milk","Protein+rice+salad","Protein+sweet potato+veggies","Shake+banana",
                "Toast+protein","Protein+salad","Grilled protein+pasta","Peanut butter toast",
                "Pancakes+protein+milk","Protein rice bowl","Protein+brown rice+broccoli","Nuts+bar",
                "Bhurji+paratha","Protein curry+rice","Protein+roti+dal","Banana shake",
                "Oats+protein+milk","Protein+quinoa+salad","Protein steak+veggies","Boiled eggs/paneer",
                "French toast+protein","Protein wrap+salad","Grilled protein+potato","Protein shake",
                "High protein rest","Protein soup+bread","Baked protein+rice","Milk+almonds"), today);

        // ── STAY FIT ─────────────────────────────────────────────────────────
        // Stay Fit + Non-Vegetarian + No Condition
        seedDiet("Stay Fit – Non-Veg", "Fitness", "Daily Health", "4 Weeks", "Stay Fit", "Non-Vegetarian", "No",
            "Balanced everyday diet with lean meats to maintain energy and overall fitness.",
            buildSchedule("Oats+banana+milk","Rice+dal+chicken salad","Grilled chicken+roti+sabzi","Fruit+nuts",
                "Poha+green tea","Roti+sabzi+curd","Fish+dal+rice","Buttermilk",
                "Idli+sambar","Chicken+roti","Mixed veg+dal+rice","Apple",
                "Upma+juice","Chicken/fish+roti","Dal khichdi+curd","Roasted chana",
                "Smoothie+toast","Chicken biryani+raita","Grilled fish+veggies","Yogurt",
                "Dalia+milk","Rajma+rice","Chicken+roti+dal","Mixed nuts",
                "Light meal","Chicken soup+bread","Khichdi+salad","Coconut water"), today);
        // Stay Fit + Vegetarian + No Condition
        seedDiet("Stay Fit – Veg", "Fitness", "Daily Health", "4 Weeks", "Stay Fit", "Vegetarian", "No",
            "Balanced vegetarian diet to maintain energy, health and overall fitness.",
            buildSchedule("Oats+banana+milk","Rice+dal+salad","Paneer+roti+sabzi","Fruit+nuts",
                "Poha+green tea","Roti+sabzi+curd","Dal+rice+salad","Buttermilk",
                "Idli+sambar","Chole+roti","Mixed veg+dal+rice","Apple",
                "Upma+juice","Paneer+roti","Dal khichdi+curd","Roasted chana",
                "Smoothie+toast","Veg biryani+raita","Tofu+veggies","Yogurt",
                "Dalia+milk","Rajma+rice","Sabzi+roti+dal","Mixed nuts",
                "Light meal","Veg soup+bread","Khichdi+salad","Coconut water"), today);
        // Stay Fit + Vegan + No Condition
        seedDiet("Stay Fit – Vegan", "Nutrition", "Daily Health", "4 Weeks", "Stay Fit", "Vegan", "No",
            "100% plant-based balanced diet to maintain daily health and energy.",
            buildSchedule("Smoothie bowl+chia seeds","Quinoa+chickpea salad","Lentil curry+brown rice","Almonds+dates",
                "Oats+almond milk+berries","Tofu stir fry+rice","Black bean curry+roti","Pumpkin seeds",
                "Avocado toast+green tea","Veg wrap+hummus","Dal+sabzi+rice","Fruit bowl",
                "Dalia+coconut milk","Sprouts salad+roti","Soya chunks+veggies","Walnuts",
                "Banana pancakes (vegan)","Chickpea salad+bread","Tofu curry+rice","Roasted makhana",
                "Smoothie+flaxseeds","Veg soup+multigrain bread","Rajma+rice","Coconut water",
                "Raw fruits","Salad bowl","Khichdi (vegan)","Herbal tea+nuts"), today);
        // Stay Fit + No Preference + No Condition
        seedDiet("Stay Fit – General", "Fitness", "Daily Health", "4 Weeks", "Stay Fit", "No Preference", "No",
            "Flexible balanced diet for any preference to maintain overall fitness.",
            buildSchedule("Oats+banana+milk","Rice+dal+salad","Protein+roti+sabzi","Fruit+nuts",
                "Poha+green tea","Roti+sabzi+curd","Dal+rice+salad","Buttermilk",
                "Idli+sambar","Chole+roti","Mixed veg+dal+rice","Apple",
                "Upma+juice","Protein+roti","Dal khichdi+curd","Roasted chana",
                "Smoothie+toast","Biryani+raita","Grilled protein+veggies","Yogurt",
                "Dalia+milk","Rajma+rice","Sabzi+roti+dal","Mixed nuts",
                "Light meal","Veg soup+bread","Khichdi+salad","Coconut water"), today);
        // Stay Fit + Diabetes
        seedDiet("Stay Fit – Diabetic", "Medical", "Blood Sugar Control", "6 Weeks", "Stay Fit", "No Preference", "Diabetes",
            "Low glycemic index balanced diet to maintain fitness while managing blood sugar.",
            buildSchedule("Oats+skimmed milk (no sugar)","Brown rice+dal+salad","Grilled protein+veggies","Cucumber+nuts",
                "Methi paratha+curd","Multigrain roti+sabzi+dal","Baked protein+salad","Buttermilk",
                "Poha (less oil)+green tea","Rajma+brown rice","Steamed protein+veggies","Apple slices",
                "Upma+green tea","Chole+roti+salad","Grilled protein+broccoli","Roasted makhana",
                "Egg white/paneer+toast","Veg soup+multigrain roti","Dal+sabzi+small rice","Yogurt (no sugar)",
                "Dalia+milk (no sugar)","Sprouts+roti","Baked protein+salad","Walnuts",
                "Low sugar fruits","Veg salad+soup","Khichdi+curd","Coconut water"), today);
        // Stay Fit + Thyroid
        seedDiet("Stay Fit – Thyroid", "Medical", "Thyroid Health", "8 Weeks", "Stay Fit", "No Preference", "Thyroid",
            "Selenium and iodine-rich balanced diet to support thyroid function.",
            buildSchedule("Brazil nuts+oats+milk","Tuna/paneer salad+brown rice","Grilled fish+veggies","Pumpkin seeds",
                "Eggs/paneer+whole wheat toast","Chicken/tofu+quinoa+salad","Baked fish+sweet potato","Walnuts",
                "Smoothie+chia seeds","Seafood/paneer+roti","Dal+sabzi+rice","Sunflower seeds",
                "Oats+berries","Tuna wrap/veg wrap+salad","Grilled protein+broccoli","Almonds",
                "Egg omelette/paneer+toast","Chicken soup/veg soup+bread","Salmon/tofu+veggies","Fruit bowl",
                "Dalia+milk","Sprouts+roti","Fish curry/dal+rice","Roasted chana",
                "Selenium foods","Veg soup+bread","Khichdi+curd","Coconut water"), today);
        // Stay Fit + PCOS
        seedDiet("Stay Fit – PCOS", "Women Health", "Hormone Balance", "6 Weeks", "Stay Fit", "Vegetarian", "PCOS / Hormonal",
            "Anti-inflammatory hormone-balancing diet to manage PCOS symptoms.",
            buildSchedule("Flaxseed smoothie+nuts","Quinoa salad+paneer","Dal+sabzi+roti","Pumpkin seeds",
                "Oats+berries+milk","Chole+brown rice","Tofu stir fry+veggies","Walnuts",
                "Moong dal chilla+curd","Sprouts salad+roti","Palak paneer+roti","Sunflower seeds",
                "Dalia+milk","Rajma+rice+salad","Mixed veg+dal","Fruit bowl",
                "Smoothie bowl+chia seeds","Paneer bhurji+roti","Soya curry+rice","Almonds",
                "Idli+sambar","Veg soup+multigrain roti","Dal khichdi+curd","Roasted makhana",
                "Anti-inflammatory foods","Turmeric milk+salad","Khichdi+curd","Coconut water"), today);

        // ── FLEXIBILITY ──────────────────────────────────────────────────────
        // Flexibility + Vegetarian + No Condition
        seedDiet("Flexibility – Veg", "Fitness", "Flexibility & Mobility", "4 Weeks", "Flexibility", "Vegetarian", "No",
            "Anti-inflammatory vegetarian diet to support flexibility training and joint health.",
            buildSchedule("Turmeric milk+banana","Quinoa salad+paneer","Dal+sabzi+roti","Walnuts+berries",
                "Smoothie+chia seeds","Chole+brown rice","Palak paneer+roti","Pumpkin seeds",
                "Oats+berries+milk","Sprouts salad+roti","Tofu stir fry+rice","Coconut water",
                "Idli+sambar","Paneer bhurji+roti","Mixed veg+dal","Almonds",
                "Dalia+milk","Rajma+rice+salad","Soya curry+roti","Fruit bowl",
                "Poha+green tea","Veg soup+bread","Dal khichdi+curd","Herbal tea",
                "Hydrating foods","Cucumber salad+soup","Khichdi+curd","Coconut water"), today);
        // Flexibility + Non-Vegetarian + No Condition
        seedDiet("Flexibility – Non-Veg", "Fitness", "Flexibility & Mobility", "4 Weeks", "Flexibility", "Non-Vegetarian", "No",
            "Anti-inflammatory diet with omega-3 rich foods to support joint health and flexibility.",
            buildSchedule("Turmeric milk+banana+eggs","Salmon salad+brown rice","Grilled fish+veggies+roti","Walnuts+berries",
                "Smoothie+chia seeds","Chicken+brown rice","Baked fish+veggies","Pumpkin seeds",
                "Oats+berries+milk","Tuna salad+roti","Grilled chicken+rice","Coconut water",
                "Eggs+toast","Fish curry+roti","Mixed veg+chicken","Almonds",
                "Dalia+milk","Salmon+quinoa+salad","Grilled fish+roti","Fruit bowl",
                "Poha+green tea","Chicken soup+bread","Fish+dal+rice","Herbal tea",
                "Hydrating foods","Cucumber salad+soup","Khichdi+curd","Coconut water"), today);
        // Flexibility + Vegan + No Condition
        seedDiet("Flexibility – Vegan", "Nutrition", "Flexibility & Mobility", "4 Weeks", "Flexibility", "Vegan", "No",
            "100% plant-based anti-inflammatory diet rich in omega-3 for joint health.",
            buildSchedule("Turmeric smoothie+chia seeds","Quinoa+chickpea salad","Lentil curry+brown rice","Walnuts+berries",
                "Oats+almond milk+flaxseeds","Tofu stir fry+rice","Black bean curry+roti","Pumpkin seeds",
                "Avocado toast+green tea","Veg wrap+hummus","Dal+sabzi+rice","Coconut water",
                "Dalia+coconut milk","Sprouts salad+roti","Soya chunks+veggies","Almonds",
                "Banana+chia seeds","Chickpea salad+bread","Tofu curry+rice","Fruit bowl",
                "Smoothie+flaxseeds","Veg soup+bread","Rajma+rice","Herbal tea",
                "Hydrating fruits","Salad bowl","Khichdi (vegan)","Coconut water"), today);
        // Flexibility + No Preference + No Condition
        seedDiet("Flexibility – General", "Fitness", "Flexibility & Mobility", "4 Weeks", "Flexibility", "No Preference", "No",
            "Flexible anti-inflammatory diet for any preference to support mobility.",
            buildSchedule("Turmeric milk+banana","Quinoa salad+protein","Dal+sabzi+roti","Walnuts+berries",
                "Smoothie+chia seeds","Protein+brown rice","Baked protein+veggies","Pumpkin seeds",
                "Oats+berries+milk","Salad+roti","Mixed veg+protein+rice","Coconut water",
                "Toast+protein","Protein curry+roti","Mixed veg+dal","Almonds",
                "Dalia+milk","Protein+quinoa+salad","Grilled protein+roti","Fruit bowl",
                "Poha+green tea","Soup+bread","Dal+rice","Herbal tea",
                "Hydrating foods","Cucumber salad+soup","Khichdi+curd","Coconut water"), today);
        // Flexibility + PCOS
        seedDiet("Flexibility – PCOS", "Women Health", "Flexibility + Hormone Balance", "6 Weeks", "Flexibility", "Vegetarian", "PCOS / Hormonal",
            "Anti-inflammatory diet combining flexibility support with PCOS hormone management.",
            buildSchedule("Flaxseed smoothie+chia seeds","Quinoa salad+paneer","Dal+sabzi+roti","Pumpkin seeds+walnuts",
                "Turmeric milk+banana","Chole+brown rice","Tofu stir fry+veggies","Sunflower seeds",
                "Oats+berries+milk","Sprouts salad+roti","Palak paneer+roti","Coconut water",
                "Dalia+milk","Rajma+rice+salad","Mixed veg+dal","Almonds",
                "Smoothie bowl+chia seeds","Paneer bhurji+roti","Soya curry+roti","Fruit bowl",
                "Idli+sambar","Veg soup+multigrain roti","Dal khichdi+curd","Herbal tea",
                "Anti-inflammatory foods","Turmeric milk+salad","Khichdi+curd","Coconut water"), today);
    }
    private void seedDiet(String name, String category, String target, String duration,
                          String forGoal, String forDiet, String forCondition,
                          String description, String schedule, String created) {
        DietPlan p = new DietPlan();
        p.setPlanName(name); p.setCategory(category); p.setTarget(target);
        p.setDuration(duration); p.setForGoal(forGoal); p.setForDiet(forDiet);
        p.setForCondition(forCondition); p.setDescription(description);
        p.setWeeklySchedule(schedule); p.setStatus("Active"); p.setCreated(created);
        dietPlanRepository.save(p);
    }

    private void seedPlan(String name, String category, String target, String duration, String created) {
        DietPlan p = new DietPlan();
        p.setPlanName(name); p.setCategory(category);
        p.setTarget(target); p.setDuration(duration);
        p.setStatus("Active"); p.setCreated(created);
        dietPlanRepository.save(p);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email").toLowerCase().trim();
        if (userRepository.findByEmail(email).isPresent())
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email already registered"));
        User user = new User(body.get("name"), email, body.get("password"), body.getOrDefault("mobile", ""), "user");
        user.setJoinedDate(LocalDate.now().toString());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Account created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email").toLowerCase().trim();
        String password = body.get("password");

        if (email.contains("admin") && ADMIN_PASSWORD.equals(password)) {
            return ResponseEntity.ok(Map.of(
                "success", true, "redirect", "/admin/dashboard",
                "name", "Admin", "role", "admin", "userId", 0,
                "setupDone", true, "theme", "dark"
            ));
        }

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty())
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "No account found with this email"));

        User user = opt.get();
        if ("Blocked".equals(user.getStatus()))
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "Your account has been blocked. Contact admin."));

        String storedPwd = user.getDisplayPwd() != null ? user.getDisplayPwd() : user.getPassword();
        if (!storedPwd.equals(password))
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "Incorrect password"));

        boolean setupDone = user.getSetupAnswers() != null && !user.getSetupAnswers().isEmpty();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "redirect", "/user/dashboard",
            "name", user.getName(),
            "role", "user",
            "userId", user.getId(),
            "email", user.getEmail(),
            "theme", user.getTheme() != null ? user.getTheme() : "dark",
            "setupDone", setupDone,
            "goal", user.getGoal() != null ? user.getGoal() : ""
        ));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        User user = opt.get();
        if (body.containsKey("name")) user.setName(body.get("name"));
        if (body.containsKey("mobile")) user.setMobile(body.get("mobile"));
        if (body.containsKey("age")) user.setAge(body.get("age"));
        if (body.containsKey("gender")) user.setGender(body.get("gender"));
        if (body.containsKey("height")) user.setHeight(body.get("height"));
        if (body.containsKey("weight")) user.setWeight(body.get("weight"));
        if (body.containsKey("goal")) user.setGoal(body.get("goal"));
        if (body.containsKey("activityLevel")) user.setActivityLevel(body.get("activityLevel"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Profile updated"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        User user = opt.get();
        String stored = user.getDisplayPwd() != null ? user.getDisplayPwd() : user.getPassword();
        if (!stored.equals(oldPwd)) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Current password is incorrect"));
        user.setDisplayPwd(newPwd);
        user.setPassword("*".repeat(newPwd.length()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true, "message", "Password changed successfully"));
    }

    @PostMapping("/save-setup")
    public ResponseEntity<?> saveSetup(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        User user = opt.get();
        if (body.containsKey("answers")) user.setSetupAnswers(body.get("answers").toString());
        if (body.containsKey("goal")) user.setGoal((String) body.get("goal"));
        if (body.containsKey("activityLevel")) user.setActivityLevel((String) body.get("activityLevel"));
        // Save diet preference and health condition directly
        if (body.containsKey("dietPreference") && body.get("dietPreference") != null)
            user.setDietPreference((String) body.get("dietPreference"));
        if (body.containsKey("healthCondition") && body.get("healthCondition") != null)
            user.setHealthCondition((String) body.get("healthCondition"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "goal", user.getGoal() != null ? user.getGoal() : "",
            "activityLevel", user.getActivityLevel() != null ? user.getActivityLevel() : "",
            "setupAnswers", user.getSetupAnswers() != null ? user.getSetupAnswers() : "",
            "dietPreference", user.getDietPreference() != null ? user.getDietPreference() : "",
            "healthCondition", user.getHealthCondition() != null ? user.getHealthCondition() : ""
        ));
    }

    @PostMapping("/save-theme")
    public ResponseEntity<?> saveTheme(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String theme = body.get("theme");
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.ok(Map.of("success", false));
        User user = opt.get();
        user.setTheme(theme);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String email) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false));
        User u = opt.get();
        Map<String, Object> data = new HashMap<>();
        data.put("id", u.getId());
        data.put("name", u.getName());
        data.put("email", u.getEmail());
        data.put("mobile", u.getMobile() != null ? u.getMobile() : "");
        data.put("age", u.getAge() != null ? u.getAge() : "");
        data.put("gender", u.getGender() != null ? u.getGender() : "");
        data.put("height", u.getHeight() != null ? u.getHeight() : "");
        data.put("weight", u.getWeight() != null ? u.getWeight() : "");
        data.put("goal", u.getGoal() != null ? u.getGoal() : "");
        data.put("activityLevel", u.getActivityLevel() != null ? u.getActivityLevel() : "");
        data.put("joinedDate", u.getJoinedDate() != null ? u.getJoinedDate() : "");
        data.put("theme", u.getTheme() != null ? u.getTheme() : "dark");
        data.put("setupAnswers", u.getSetupAnswers() != null ? u.getSetupAnswers() : "");
        data.put("setupDone", u.getSetupAnswers() != null && !u.getSetupAnswers().isEmpty());
        data.put("dietPreference", u.getDietPreference() != null ? u.getDietPreference() : "");
        data.put("healthCondition", u.getHealthCondition() != null ? u.getHealthCondition() : "");
        return ResponseEntity.ok(data);
    }

    // Progress tracking
    @PostMapping("/progress/log")
    public ResponseEntity<?> logProgress(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
        UserProgress p = new UserProgress();
        p.setUserId(opt.get().getId());
        p.setDate(LocalDate.now().toString());
        if (body.get("weight") != null) p.setWeight(Double.parseDouble(body.get("weight").toString()));
        if (body.get("workoutMinutes") != null) p.setWorkoutMinutes(Integer.parseInt(body.get("workoutMinutes").toString()));
        if (body.get("waterGlasses") != null) p.setWaterGlasses(Integer.parseInt(body.get("waterGlasses").toString()));
        if (body.get("caloriesBurned") != null) p.setCaloriesBurned(Integer.parseInt(body.get("caloriesBurned").toString()));
        if (body.get("notes") != null) p.setNotes((String) body.get("notes"));
        userProgressRepository.save(p);
        return ResponseEntity.ok(Map.of("success", true, "message", "Progress logged!"));
    }

    @GetMapping("/progress/history")
    public ResponseEntity<?> getProgressHistory(@RequestParam String email) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(userProgressRepository.findByUserIdOrderByDateAsc(opt.get().getId()));
    }

    // Get recommended plans based on user's setup answers
    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(@RequestParam String email) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.ok(Map.of("workouts", List.of(), "diets", List.of()));

        User user = opt.get();
        String goal = user.getGoal() != null ? user.getGoal() : "";
        String level = user.getActivityLevel() != null ? user.getActivityLevel() : "";

        // Map activity level to workout level
        String workoutLevel = "Beginner";
        if (level.contains("Moderately") || level.contains("Some")) workoutLevel = "Intermediate";
        else if (level.contains("Very Active") || level.contains("Regular")) workoutLevel = "Advanced";

        // Get matching workouts
        List<WorkoutPlan> workouts;
        if (!goal.isEmpty()) {
            workouts = workoutPlanRepository.findByGoalContainingIgnoreCaseAndStatus(goal, "Active");
            if (workouts.isEmpty()) workouts = workoutPlanRepository.findByStatus("Active");
        } else {
            workouts = workoutPlanRepository.findByStatus("Active");
        }

        // Get matching diets
        List<DietPlan> diets = dietPlanRepository.findByStatus("Active");

        return ResponseEntity.ok(Map.of("workouts", workouts, "diets", diets, "goal", goal, "level", workoutLevel));
    }
}

