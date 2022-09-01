package co.develhope.testController;

import co.develhope.testController.controller.UserController;
import co.develhope.testController.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
class UserTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserController controller;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

	private User getUserFromId(Long id) throws Exception{
		MvcResult result = this.mockMvc.perform(get("/users/" + id))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		try {
			String userJSON = result.getResponse().getContentAsString();
			User user = objectMapper.readValue(userJSON, User.class);

			assertThat(user).isNotNull();
			assertThat(user.getId()).isNotNull();

			return user;
		}catch (Exception e){
			return null;
		}
	}

	private User createAUser() throws Exception {
		User user = new User();
		user.setUserName("giggino");
		user.setCity("milazzo");
		return createAUser(user);
	}

	private User createAUser(User user) throws Exception {
		MvcResult result = createAUserRequest(user);
		User userFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

		assertThat(userFromResponse).isNotNull();
		assertThat(userFromResponse.getId()).isNotNull();

		return userFromResponse;
	}

	private MvcResult createAUserRequest() throws Exception {
		User user = new User();
		user.setUserName("giggino");
		user.setCity("milazzo");
		return createAUserRequest(user);
	}

	private MvcResult createAUserRequest(User user) throws Exception {
		if(user == null) return null;
		String userJSON = objectMapper.writeValueAsString(user);
		return this.mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(userJSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
	}

	@Test
	void createAUserTest() throws Exception {
		User userFromResponse = createAUser();
	}

	@Test
	void readUserList() throws Exception {
		createAUserRequest();

		MvcResult result =this.mockMvc.perform(get("/users/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		List<User> usersFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
		System.out.println("Users in database are: " + usersFromResponse.size());
		assertThat(usersFromResponse.size()).isNotZero();
	}

	@Test
	void readSingleUser() throws Exception {
		User user = createAUser();
		User userFromResponse = getUserFromId(user.getId());
		assertThat(userFromResponse.getId()).isEqualTo(user.getId());
	}

	@Test
	void updateUser() throws Exception{
		User user = createAUser();
		String newUserName = "Bilboa";
		user.setUserName(newUserName);

		String userJSON = objectMapper.writeValueAsString(user);

		MvcResult result = this.mockMvc.perform(put("/users/"+user.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(userJSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();
		User studentFromResponse = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

		assertThat(studentFromResponse.getId()).isEqualTo(user.getId());
		assertThat(studentFromResponse.getUserName()).isEqualTo(newUserName);

		User studentFromResponseGet = getUserFromId(user.getId());
		assertThat(studentFromResponseGet.getId()).isEqualTo(user.getId());
		assertThat(studentFromResponseGet.getUserName()).isEqualTo(newUserName);
	}

	@Test
	void deleteUser() throws Exception{
		User user = createAUser();
		assertThat(user.getId()).isNotNull();

		this.mockMvc.perform(delete("/users/"+user.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		User userFromResponseGet = getUserFromId(user.getId());
		assertThat(userFromResponseGet).isNull();
	}
}
