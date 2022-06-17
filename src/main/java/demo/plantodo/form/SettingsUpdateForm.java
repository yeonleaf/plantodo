package demo.plantodo.form;

import demo.plantodo.converter.StringToPermStatusConverter;
import demo.plantodo.domain.PermStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SettingsUpdateForm {
    private PermStatus notification_perm;
    private boolean deadline_alarm;
    private int deadline_alarm_term;

    public SettingsUpdateForm(PermStatus notification_perm, boolean deadline_alarm, int deadline_alarm_term) {
        this.notification_perm = notification_perm;
        this.deadline_alarm = deadline_alarm;
        this.deadline_alarm_term = deadline_alarm_term;
    }

    /*String이 들어오는 경우*/
    public SettingsUpdateForm(String notification_perm, boolean deadline_alarm, int deadline_alarm_term) {
        this.notification_perm = new StringToPermStatusConverter().convert(notification_perm);
        this.deadline_alarm = deadline_alarm;
        this.deadline_alarm_term = deadline_alarm_term;
    }
}
