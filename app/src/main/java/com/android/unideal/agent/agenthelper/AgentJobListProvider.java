package com.android.unideal.agent.agenthelper;

import com.android.unideal.data.JobDetails;
import com.android.unideal.data.Ratings;
import com.android.unideal.data.Transaction;
import com.android.unideal.data.TransactionMode;
import com.android.unideal.data.User;
import com.android.unideal.util.AppConstants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by ADMIN on 26-09-2016.
 */

public class AgentJobListProvider {
  private static List<User> randomUser = new ArrayList<>();
  private static List<JobDetails> randomJobDetails = new ArrayList<>();
  private static List<Ratings> ratingsList = new ArrayList<>();
  private static List<Transaction> transactionList = new ArrayList<>();
  private static User sender;
  private static User receiver;
  private static String filePath = "Document.pdf";
  private static String bioData =
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam fringilla, augue ac malesuada hendrerit, est eros pellentesque massa, maximus mattis mi ligula in diam. Sed facilisis neque eu egestas elementum. Suspendisse et faucibus eros. Nullam ornare porta urna at vestibulum. Aenean faucibus nisl vitae mi porttitor, ac condimentum justo auctor. Vestibulum at nisl ornare, imperdiet libero at, rhoncus sem. Fusce vel odio et mi tempor cursus. Cras tincidunt elementum dui, a ullamcorper purus imperdiet ac. Proin maximus ullamcorper consequat. Pellentesque auctor lobortis gravida. Praesent ultricies nunc eu sem elementum semper. Nulla justo urna, tristique nec orci nec, porttitor commodo neque. Morbi tincidunt feugiat risus malesuada placerat";
  private static String[] imagePath = {
      "http://www.american.edu/uploads/profiles/large/chris_palmer_profile_11.jpg",
      "http://bjstlh.com/data/wallpapers/65/WDF_1102596.jpg",
      "http://bjstlh.com/data/wallpapers/65/WDF_1102425.jpg",
      "https://www.cheme.cornell.edu/engineering2/customcf/iws_ai_faculty_display/ai_images/mjp31-profile.jpg",
      "http://dentistbaires.com/uploads/profile-people-ops-ryan-vflo-YEqs.jpg",
      "http://54.165.17.179/assets/images/people/tiles/karlene-quigley-large.jpg",
      "https://www.morganstanley.com/assets/images/people/tiles/audrey-choi-large.jpg",
      "http://www.morganstanley.com/assets/images/people/tiles/jessica-alsford-large.jpg"
  };

  static {
    randomJobDetailsLoad();
  }

  private static void randomJobDetailsLoad() {

    randomJobDetails.add(new JobDetails().setJobTitle(
        "How to file a complaint against a retailer with the Ministry of Government?")
        .setDescription(
            "How to file a complaint against a retailer with the Ministry of Government?")
        .setConsignmentSize(randInt(0, 100))
        .setAgentPrice(randInt(100, 10000)));

    randomJobDetails.add(
        new JobDetails().setJobTitle("What are the different type of Taxation's in the accounting?")
            .setDescription(
                "Hey, I want to know, What are the different type of Taxation's in the account book keeping? If anybody who know kindly please contact me here")
            .setConsignmentSize(randInt(0, 100))
            .setAgentPrice(randInt(100, 10000)));

    randomJobDetails.add(new JobDetails().setJobTitle(
        "Is it possible to tap the energy generated while an elevator goes down a high rise building?")
        .setDescription(
            "Is it possible to tap the energy generated while an elevator goes down a high rise building?")
        .setConsignmentSize(randInt(0, 100))
        .setAgentPrice(randInt(100, 10000)));

    randomJobDetails.add(new JobDetails().setJobTitle(
        "How to file a complaint against a retailer with the Ministry of Government?")
        .setDescription(
            "How to file a complaint against a retailer with the Ministry of Government?")
        .setConsignmentSize(randInt(0, 100))
        .setAgentPrice(randInt(100, 10000)));

    randomJobDetails.add(new JobDetails().setJobTitle("Design a business logo.")
        .setDescription(
            "I run a sales and marketing business based in Surfers on the Gold Coast. My brand is Hypertrophy Marketing and takes its values from the state created in a person's body when they exercise to the point where muscles are forced to grow. We work smart and take on tasks that may take work, but ultimately grow the size and ability of the team in a sustainable manner. We work through the burn until we not only complete the task, but increase our capabilities as a result! I'd be very grateful for someone to design a logo to reflect the values and brand of Hypertrophy Marketing")
        .setConsignmentSize(randInt(0, 100))
        .setAgentPrice(randInt(100, 10000)));

    randomJobDetails.add(new JobDetails().setJobTitle("Weeding and trimming")
        .setDescription("Small back yard needs some weeding and trimming of plants")
        .setConsignmentSize(randInt(0, 100))
        .setAgentPrice(randInt(100, 10000)));

    randomJobDetails.add(new JobDetails().setJobTitle("Facebook group interaction needed")
        .setDescription(getDescription())
        .setConsignmentSize(randInt(0, 100))
        .setAgentPrice(randInt(100, 10000)));

    randomJobDetails.add(
        new JobDetails().setJobTitle("Ikea Tempe to Paddington lift - with furniture")
            .setDescription(getDescription())
            .setConsignmentSize(randInt(0, 100))
            .setAgentPrice(randInt(100, 10000)));

    //randomUser.add(new User().setName("Lauren")
    //    .setTotalUnReadMessage(randInt(0, 30))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Mark")
    //    .setTotalUnReadMessage(randInt(0, 20))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Nicholas")
    //    .setTotalUnReadMessage(randInt(0, 20))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Nicholas")
    //    .setTotalUnReadMessage(randInt(0, 10))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Joshua")
    //    .setTotalUnReadMessage(randInt(0, 10))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Richard")
    //    .setTotalUnReadMessage(randInt(0, 10))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randInt(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Nancy")
    //    .setTotalUnReadMessage(randInt(0, 10))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Nicholas")
    //    .setTotalUnReadMessage(randInt(0, 10))
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Anthony")
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));
    //randomUser.add(new User().setName("Bobby")
    //    .setImageUrl(imagePath[randInt(0, imagePath.length - 1)])
    //    .setUserRatings(new Ratings().setRatings(randFloat(0, 4)))
    //    .setDocumentLink(filePath)
    //    .setSkills(bioData));

    ratingsList.add(new Ratings().setRatings(4.5f));
    ratingsList.add(new Ratings().setRatings(4.0f));
    ratingsList.add(new Ratings().setRatings(3.0f));
    ratingsList.add(new Ratings().setRatings(5.0f));
    ratingsList.add(new Ratings().setRatings(2.5f));
    ratingsList.add(new Ratings().setRatings(2.0f));

    if (randomUser.size() > 0) {
      sender = randomUser.get(randInt(0, randomUser.size() - 1));
      receiver = randomUser.get(randInt(0, randomUser.size() - 1));
    }
  }

  private static String getRandomDate() {
    Date currentDate = new Date(System.currentTimeMillis());
    SimpleDateFormat mDateFormat =
        new SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.getDefault());
    return mDateFormat.format(currentDate);
  }

  public static List<Transaction> getTransactionList() {
    return transactionList;
  }

  public static int randInt(int min, int max) {
    return new Random().nextInt((max - min) + 1) + min;
  }

  public static float randFloat(float min, float max) {
    return new Random().nextFloat() * (min - max) + min;
  }

  public static List<User> getUserList() {
    return randomUser;
  }

  private static String getDescription() {
    return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur fermentum, nunc ac venenatis mollis, orci lorem vestibulum nibh, a consectetur felis sem vel magna. Phasellus rutrum semper bibendum. Proin posuere lacus eget magna pharetra, vel vulputate mi consectetur. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aliquam pharetra dui non lacus vulputate, ut finibus odio sollicitudin. Donec viverra id nunc quis condimentum. Pellentesque nec justo sed diam bibendum suscipit ac ac dui. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam nec massa vitae eros cursus blandit. Mauris accumsan lectus mauris, in rhoncus arcu pharetra et. Quisque aliquam rutrum sodales. Aliquam vel libero nulla.";
  }
}
