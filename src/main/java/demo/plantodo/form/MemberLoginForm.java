package demo.plantodo.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class MemberLoginForm {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
