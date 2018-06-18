package com.cvvid.apicall;

/**
 * Created by webandmobile on 04/10/2017.
 */

public class LOCATION_URL {

    public static final String CONFIG_URL = "http://pdev.work/cvvidapi/api";

    //JSON URL
    public static final String CANDIDATE_DETAIL = CONFIG_URL+"/fetchcandidatedetails/id/";
    public static final String LOGIN = CONFIG_URL+"/login";
    public static final String INSTITUTION_DETAIL = CONFIG_URL+"/getinstitutions/id/";
    public static final String PROFILE_COMPLETE = CONFIG_URL+"/getprofilecompletion/profile_id/";
    public static final String CHECK_INTERVIEW = CONFIG_URL+"/checkcandidateinterview/id/";
    public static final String PROFILE_VIDEOS = CONFIG_URL+"/getallprofilevideos/id/";
    public static final String PROFILE_DOCUMENTS = CONFIG_URL+"/getallprofiledocumentation/id/";
    public static final String PROFILE_EXPERENCES = CONFIG_URL+"/getallprofessionalexperience/id/";
    public static final String CAREER_EXPERENCES = CONFIG_URL+"/getcarrerexperience/id/";
    public static final String PROFILE_EDUCATION = CONFIG_URL+"/getallusereducation/id/";
    public static final String PROFILE_HOBBIES = CONFIG_URL+"/gethobbies/id/";
    public static final String PROFILE_LANGUAGE = CONFIG_URL+"/getlanguage/id/";
    public static final String VIDEO_EDIT = CONFIG_URL+"/updateVideoName/";
    public static final String VIDEO_DELETE = CONFIG_URL+"/profileVideoDelete";
    public static final String EMPLOEYR_VIDEO_DELETE = CONFIG_URL+"/deleteCompanyProfile/id/";
    public static final String VIDEO_PUBLISH = CONFIG_URL+"/profileVideoDefault";
    public static final String SAVE_MES = CONFIG_URL+"/sendmessages/id/";
    public static final String APPLY_JOB = CONFIG_URL+"/appliedjob/";
    public static final String PROFILE_VIEWS = CONFIG_URL+"/getallprofileviews/profile_id/";
    public static final String CATEGORY = CONFIG_URL+"/getallcategory";
    public static final String SKILLS = CONFIG_URL+"/getallskills/id/";
    public static final String Questions = CONFIG_URL+"/getallquestions/job_id/";
    public static final String CURRENT_Questions = CONFIG_URL+"/getcurrentquestions/job_id/";
    public static final String INDUSTRY =  CONFIG_URL+"/getIndustry";
    public static final String USER_ALL =  CONFIG_URL+"/getalljobusers/id/";
    public static final String SAVE_PHOTO =  CONFIG_URL+"/updateprofilepicture";

    public static final String EMPLOYER_REGISTER = CONFIG_URL+"/createemployer";
    public static final String EMPLOYER_DETAIL = CONFIG_URL+"/fetchemployerprofiledetails/id/";
    public static final String EMPLOYER_BACKGROUND_GET = CONFIG_URL+"/getcompanybackground/id/";
    public static final String EMPLOYER_BACKGROUND_EDIT = CONFIG_URL+"/editbody/id/";
    public static final String EMPLOYER_CREATE_VIDEO = CONFIG_URL+"/createemployervideo/id/";
    public static final String EMPLOYER_GET_VIDEO = CONFIG_URL+"/getcompanyprofilevideos/id/";

    public static final String CREATE_EMPLOYER_USER = CONFIG_URL+"/adduser/id/";
    public static final String URL_GET_EMPLOYERUSER = CONFIG_URL+"/fetchuser/id/";
    public static final String URL_EDIT_EMPLOYERUSER = CONFIG_URL+"/updateuser/id/";
    public static final String URL_DELETE_EMPLOYERUSER = CONFIG_URL+"/updateuser/id/";
    public static final String INVITE_INTERVIEW = CONFIG_URL+"/inviteforinterview/id/";

    public static final String URL_FETCH_JOB = CONFIG_URL+"/getjob/id/";
    public static final String URL_UPDATE_JOB = CONFIG_URL+"/updatenewjob/id/";
    public static final String ASSIGN_JOB = CONFIG_URL+"/assignjob/id/";
    public static final String URL_CREATE_JOB = CONFIG_URL+"/addnewjob/id/";

//    institution

    public static final String INSTITUTE_DETAIL = CONFIG_URL+"/getinstitutions/id/";
    public static final String SCHOOL_BACKGROUND_GET = CONFIG_URL+"/getschoolcompany/id/";
    public static final String INSTITUTE_BACKGROUND_EDIT = CONFIG_URL+"/editschoolbody/id/";

    //Tags used in the JSON String
    public static final String TAG_id = "id";
    public static final String TAG_NAME = "name";

    //JSON array name
    public static final String JSON_ARRAY_COUNTRY = "countries";
    public static final String JSON_ARRAY_STATES = "state";
    public static final String JSON_ARRAY_CITY = "city";
    public static final String JSON_ARRAY_LANGUAGE = "language";

    public static final String YOUR_PUBLISHABLE_KEY_TEST_HERE = "pk_test_BgbyUBWQhgPsWwHHk42LacXL";

}
