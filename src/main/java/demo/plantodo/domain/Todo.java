package demo.plantodo.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Todo {
    @Id @GeneratedValue
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    private String title;

    private int repOption;

    @ElementCollection(fetch = LAZY)
    private List<String> repValue = new ArrayList<>();


    public Todo() {
    }

    public Todo(Member member, Plan plan, String title, int repOption, List<String> repValue) {
        this.member = member;
        this.plan = plan;
        this.title = title;
        this.repOption = repOption;
        this.repValue = repValue;
    }

}
