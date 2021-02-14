package devs.mrp.gullproject.domains;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StringWrapper {
	/**
	 * Para evitar marshalling y demarhsalling los String de JSON manualmente
	 * https://stackoverflow.com/questions/48421597/returning-fluxstring-from-spring-webflux-returns-one-string-instead-of-array-o
	 */
	String string;
}
