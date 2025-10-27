package in.jatinthakur.moneymanager.service;

import in.jatinthakur.moneymanager.dto.ExpenseDTO;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import in.jatinthakur.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final ProfileRepository profileRepo;
    private final EmailService emailService;
    private final ExpenseService expenseService;
    private final TemplateEngine templateEngine;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;


        @Scheduled(cron = "0 0 22 * * *" , zone = "IST")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Starting daily expense reminder job");
        List<ProfileEntity> profiles = profileRepo.findAll();
        for(ProfileEntity profile : profiles){
            Context context = new Context();
            context.setVariable("userName", profile.getFullName());
            context.setVariable("loginUrl", frontendUrl + "/login");

            // 2. Process the template to generate the HTML string
            // 'email/dailyReminder' is the path to your template file inside 'resources/templates/'
            String body = templateEngine.process("email/dailyReminder", context);

            // 3. Send the generated HTML email
            System.out.println("Sending email to: " + profile.getEmail());
            emailService.sendEmail(profile.getEmail(), "Daily Income & Expense Reminder", body);
        }
    }

    @Scheduled(cron = "0 0 22 * * *" , zone = "IST")
    public void sendDailyExpenseSummary(){
            log.info("Starting daily expense summary job");
            List<ProfileEntity> profiles = profileRepo.findAll();
            for(ProfileEntity profile : profiles){

                List<ExpenseDTO> todayExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
                if(!todayExpenses.isEmpty()){
                    Context context = new Context();
                    context.setVariable("userName", profile.getFullName());
                    context.setVariable("expenses", todayExpenses);
                    context.setVariable("date", LocalDate.now());

                    String body = templateEngine.process("email/dailyExpenseSummary", context);

                    System.out.println("Sending daily expense summary email to: " + profile.getEmail());
                    emailService.sendEmail(profile.getEmail(), "Your Daily Expense Summary", body);
                }
                else{
                    log.info("No expenses for today for user: {}. Skipping email.", profile.getEmail());
                }
            }

    }

}

