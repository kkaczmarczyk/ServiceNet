// package org.benetech.servicenet.web.rest;

// import org.benetech.servicenet.ServiceNetApp;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.mockito.MockitoAnnotations;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit4.SpringRunner;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// /**
//  * Test class for the ActivityFilterResource REST controller.
//  *
//  * @see ActivityFilterResource
//  */
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = ServiceNetApp.class)
// public class ActivityFilterResourceIntTest {

//     private MockMvc restMockMvc;

//     @Before
//     public void setUp() {
//         MockitoAnnotations.initMocks(this);

//         ActivityFilterResource activityFilterResource = new ActivityFilterResource();
//         restMockMvc = MockMvcBuilders
//             .standaloneSetup(activityFilterResource)
//             .build();
//     }

//     /**
//      * Test getPostalCodes
//      */
//     @Test
//     public void testGetPostalCodes() throws Exception {
//         restMockMvc.perform(get("/api/activity-filter/get-postal-codes"))
//             .andExpect(status().isOk());
//     }

//     /**
//      * Test getCounties
//      */
//     @Test
//     public void testGetCounties() throws Exception {
//         restMockMvc.perform(get("/api/activity-filter/get-counties"))
//             .andExpect(status().isOk());
//     }

//     /**
//      * Test getCities
//      */
//     @Test
//     public void testGetCities() throws Exception {
//         restMockMvc.perform(get("/api/activity-filter/get-cities"))
//             .andExpect(status().isOk());
//     }

//     /**
//      * Test getPartners
//      */
//     @Test
//     public void testGetPartners() throws Exception {
//         restMockMvc.perform(get("/api/activity-filter/get-partners"))
//             .andExpect(status().isOk());
//     }
// }
