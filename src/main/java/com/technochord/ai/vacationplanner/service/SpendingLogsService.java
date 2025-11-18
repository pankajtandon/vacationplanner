package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@RagCandidate
@Log4j2
public class SpendingLogsService {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
            @ToolParam(required = true, description = "The start date for retrieving my spending history") String startDate,
            @ToolParam(required = true, description = "The end date for retrieving my spending history") String endDate
    ) { }

    public record Response(@JsonPropertyDescription("This is a line describing what was purchased. Typically, it's the name of the purchase, sometimes, a description") String description,
                           @JsonPropertyDescription("This is the category of the purchase. Examples are 'Dining', 'Grocery', 'Travel' etc.") String category,
                           @JsonPropertyDescription("This is the date and time of the purchase.") Date dateOfTransaction,
                           @JsonPropertyDescription("This is the dollar amount of the purchase.") Double amountOfTransaction)
    {
    }

    @Tool(name = "spendingLogsService", description = "Service that returns my spending history by returning all financial transactions I have had in the specified time-period")
    public List<SpendingLogsService.Response> apply(@ToolParam SpendingLogsService.Request request) {
        log.info("Called SpendingLogsService with " + request);
        //In a real app, access a transaction service.

        List<Response> responseList = List.of(
                // Grocery
                new Response("Organic Milk - 1 Gallon", "Grocery",
                        format(2024, 10, 5), 4.99),
                new Response("Whole Wheat Bread", "Grocery",
                        format(2024, 10, 12), 3.49),
                new Response("Fresh Chicken Breast - 2lbs", "Grocery",
                        format(2024, 10, 18), 12.98),
                new Response("Roma Tomatoes", "Grocery",
                        format(2024, 11, 3), 5.47),
                new Response("Greek Yogurt - 6 pack", "Grocery",
                        format(2024, 11, 10), 6.99),
                new Response("Bananas - 3lbs", "Grocery",
                        format(2024, 11, 22), 2.17),
                new Response("Eggs - Dozen", "Grocery",
                        format(2024, 12, 4), 4.29),
                new Response("Orange Juice - 64oz", "Grocery",
                        format(2024, 12, 15), 5.99),

                // Dining
                new Response("Chipotle Burrito Bowl", "Dining",
                        format(2024, 10, 8), 12.45),
                new Response("Starbucks Latte", "Dining",
                        format(2024, 10, 15), 5.75),
                new Response("Pizza Hut Large Pizza", "Dining",
                        format(2024, 10, 23), 18.99),
                new Response("Panera Soup & Sandwich", "Dining",
                        format(2024, 11, 6), 14.50),
                new Response("Thai Restaurant Pad Thai", "Dining",
                        format(2024, 11, 14), 16.95),
                new Response("McDonald's Breakfast", "Dining",
                        format(2024, 11, 28), 8.49),
                new Response("Olive Garden Dinner", "Dining",
                        format(2024, 12, 7), 32.87),
                new Response("Dunkin' Coffee & Donut", "Dining",
                        format(2024, 12, 20), 6.25),

                // Transportation
                new Response("Shell Gas Station", "Transportation",
                        format(2024, 10, 2), 45.32),
                new Response("Uber Ride to Airport", "Transportation",
                        format(2024, 10, 16), 38.50),
                new Response("Metro Card Monthly Pass", "Transportation",
                        format(2024, 10, 1), 127.00),
                new Response("BP Gas Station", "Transportation",
                        format(2024, 11, 11), 52.18),
                new Response("Car Wash Premium", "Transportation",
                        format(2024, 11, 25), 25.00),
                new Response("Parking Garage Downtown", "Transportation",
                        format(2024, 12, 9), 18.00),

                // Entertainment
                new Response("Netflix Subscription", "Entertainment",
                        format(2024, 10, 1), 15.49),
                new Response("Movie Tickets - 2 Adults", "Entertainment",
                        format(2024, 10, 19), 28.00),
                new Response("Spotify Premium", "Entertainment",
                        format(2024, 11, 1), 10.99),
                new Response("Concert Tickets", "Entertainment",
                        format(2024, 11, 17), 125.00),
                new Response("Bowling - Game & Shoes", "Entertainment",
                        format(2024, 12, 5), 42.50),
                new Response("Barnes & Noble Books", "Entertainment",
                        format(2024, 12, 18), 34.97),

                // Utilities
                new Response("Electric Bill", "Utilities",
                        format(2024, 10, 10), 142.56),
                new Response("Internet Service", "Utilities",
                        format(2024, 10, 1), 79.99),
                new Response("Water & Sewer Bill", "Utilities",
                        format(2024, 11, 8), 68.45),
                new Response("Natural Gas Bill", "Utilities",
                        format(2024, 11, 15), 95.23),
                new Response("Cell Phone Plan", "Utilities",
                        format(2024, 12, 1), 85.00),

                // Healthcare
                new Response("CVS Pharmacy - Prescriptions", "Healthcare",
                        format(2024, 10, 14), 45.00),
                new Response("Doctor Copay", "Healthcare",
                        format(2024, 10, 22), 30.00),
                new Response("Dental Cleaning", "Healthcare",
                        format(2024, 11, 5), 125.00),
                new Response("Vitamins & Supplements", "Healthcare",
                        format(2024, 11, 19), 28.99),
                new Response("Urgent Care Visit", "Healthcare",
                        format(2024, 12, 12), 75.00),

                // Retail
                new Response("Amazon - Electronics", "Retail",
                        format(2024, 10, 6), 89.99),
                new Response("Target - Household Items", "Retail",
                        format(2024, 10, 20), 67.43),
                new Response("Nike Running Shoes", "Retail",
                        format(2024, 10, 28), 124.99),
                new Response("Best Buy - USB Cable", "Retail",
                        format(2024, 11, 9), 19.99),
                new Response("HomeGoods Decor", "Retail",
                        format(2024, 11, 18), 45.88),
                new Response("Macy's Clothing", "Retail",
                        format(2024, 12, 2), 156.20),
                new Response("IKEA Furniture", "Retail",
                        format(2024, 12, 16), 249.00),
                new Response("Costco - Bulk Purchase", "Retail",
                        format(2024, 12, 25), 187.65),

                // Fitness (Highest spending category)
                new Response("Premium Gym Membership - 3 Months", "Fitness",
                        format(2024, 10, 1), 599.99),
                new Response("Personal Training Package - 20 Sessions", "Fitness",
                        format(2024, 10, 3), 1200.00),
                new Response("Yoga Retreat Weekend", "Fitness",
                        format(2024, 10, 21), 850.00),
                new Response("High-End Peloton Bike", "Fitness",
                        format(2024, 11, 2), 1895.00),
                new Response("Nutrition Coaching - 3 Months", "Fitness",
                        format(2024, 11, 7), 675.00),
                new Response("CrossFit Membership - Quarterly", "Fitness",
                        format(2024, 11, 15), 450.00),
                new Response("Marathon Training Program", "Fitness",
                        format(2024, 12, 1), 395.00),
                new Response("Premium Protein Supplements Bundle", "Fitness",
                        format(2024, 12, 10), 285.00),
                new Response("Fitness Smartwatch", "Fitness",
                        format(2024, 12, 14), 399.99),
                new Response("Pilates Reformer Classes - 10 Pack", "Fitness",
                        format(2024, 12, 22), 425.00),

                // Education
                new Response("Udemy Online Course", "Education",
                        format(2024, 10, 11), 49.99),
                new Response("College Textbook", "Education",
                        format(2024, 10, 25), 156.00),
                new Response("Language Learning App", "Education",
                        format(2024, 11, 1), 12.99),
                new Response("Office Supplies - Staples", "Education",
                        format(2024, 11, 20), 37.82),
                new Response("Professional Certification Exam", "Education",
                        format(2024, 12, 8), 295.00),

                // Pet Care
                new Response("Petco Dog Food - 25lbs", "Pet Care",
                        format(2024, 10, 4), 54.99),
                new Response("Vet Checkup", "Pet Care",
                        format(2024, 10, 17), 85.00),
                new Response("Cat Litter & Toys", "Pet Care",
                        format(2024, 11, 12), 32.47),
                new Response("Dog Grooming", "Pet Care",
                        format(2024, 11, 26), 65.00),
                new Response("Pet Medications", "Pet Care",
                        format(2024, 12, 11), 38.50),

                // Home Improvement
                new Response("Home Depot - Paint Supplies", "Home Improvement",
                        format(2024, 10, 9), 127.86),
                new Response("Lowe's - Garden Tools", "Home Improvement",
                        format(2024, 10, 24), 89.99),
                new Response("Light Fixtures - Set of 3", "Home Improvement",
                        format(2024, 11, 4), 156.00),
                new Response("Plumber Service Call", "Home Improvement",
                        format(2024, 11, 21), 185.00),
                new Response("Ace Hardware - Fasteners", "Home Improvement",
                        format(2024, 12, 13), 24.63),

                // Personal Care
                new Response("Hair Salon - Cut & Color", "Personal Care",
                        format(2024, 10, 13), 145.00),
                new Response("Sephora Skincare Products", "Personal Care",
                        format(2024, 10, 26), 87.50),
                new Response("Barber Shop", "Personal Care",
                        format(2024, 11, 8), 35.00),
                new Response("Spa Massage - 60 min", "Personal Care",
                        format(2024, 11, 23), 95.00),
                new Response("Ulta Beauty Products", "Personal Care",
                        format(2024, 12, 6), 62.34),
                new Response("Manicure & Pedicure", "Personal Care",
                        format(2024, 12, 19), 55.00),

                // Additional mixed items to reach 100
                new Response("Whole Foods Organic Produce", "Grocery",
                        format(2024, 10, 7), 43.21),
                new Response("Trader Joe's Wine & Cheese", "Grocery",
                        format(2024, 11, 16), 28.97),
                new Response("Five Guys Burger & Fries", "Dining",
                        format(2024, 10, 29), 17.85),
                new Response("Subway Sandwich", "Dining",
                        format(2024, 11, 13), 9.99),
                new Response("Lyft Ride Home", "Transportation",
                        format(2024, 10, 30), 15.75),
                new Response("Oil Change Service", "Transportation",
                        format(2024, 11, 24), 54.99),
                new Response("Hulu Subscription", "Entertainment",
                        format(2024, 10, 1), 7.99),
                new Response("Mini Golf - Family", "Entertainment",
                        format(2024, 11, 27), 48.00),
                new Response("PlayStation Plus", "Entertainment",
                        format(2024, 12, 1), 9.99),
                new Response("Museum Admission", "Entertainment",
                        format(2024, 12, 23), 35.00),
                new Response("Trash Collection Service", "Utilities",
                        format(2024, 12, 1), 45.00),
                new Response("Eye Exam", "Healthcare",
                        format(2024, 10, 31), 125.00),
                new Response("Contact Lenses - 3 Month Supply", "Healthcare",
                        format(2024, 12, 17), 89.99),
                new Response("Walmart - Miscellaneous", "Retail",
                        format(2024, 11, 29), 73.56),
                new Response("Apple Store - AirPods", "Retail",
                        format(2024, 12, 24), 249.00),
                new Response("Spinning Class Drop-In", "Fitness",
                        format(2024, 10, 27), 35.00),
                new Response("Swimming Pool Membership", "Fitness",
                        format(2024, 11, 30), 180.00),
                new Response("LinkedIn Premium", "Education",
                        format(2024, 12, 1), 29.99),
                new Response("Dog Walking Service", "Pet Care",
                        format(2024, 12, 21), 75.00),
                new Response("Electrician Repair", "Home Improvement",
                        format(2024, 12, 26), 215.00),
                new Response("Dry Cleaning", "Personal Care",
                        format(2024, 12, 28), 32.00)
        );

        return responseList;
    }

    private Date format(int year, int month, int dayOfMonth) {
        return Date.from(LocalDate.of(year, month, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
