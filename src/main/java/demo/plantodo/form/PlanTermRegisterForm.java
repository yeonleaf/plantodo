package demo.plantodo.form;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@NoArgsConstructor
public class PlanTermRegisterForm {

    @NotBlank
    private String title;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Nullable
    private LocalTime endTime;

    /*테스트용 생성자*/
    public PlanTermRegisterForm(String title, LocalDate startDate, LocalDate endDate, @Nullable LocalTime endTime) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.endTime = endTime;
    }
}
