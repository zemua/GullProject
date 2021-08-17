package devs.mrp.gullproject.models;

import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyClient {

	@Id
	private String clientName;
	private String secret;
	private List<String> scopes;
	private List<String> authorities;
	
}
