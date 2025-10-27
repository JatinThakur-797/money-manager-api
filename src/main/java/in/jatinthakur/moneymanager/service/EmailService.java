package in.jatinthakur.moneymanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final WebClient.Builder webClientBuilder;
    private final TemplateEngine templateEngine;

    private final String fromEmail = "jt6266042@gmail.com";
    private final String fromName = "Money Manager App";

    public EmailService(WebClient.Builder webClientBuilder, TemplateEngine templateEngine) {
        this.webClientBuilder = webClientBuilder;
        this.templateEngine = templateEngine;
    }

    // --- METHOD 1: FOR YOUR NOTIFICATION SERVICE ---
    // This method accepts a pre-processed HTML string as the body.
    public void sendEmail(String to, String subject, String htmlBody) {
        this.sendEmailWithHtmlContent(to, subject, htmlBody);
    }

    // --- METHOD 2: FOR YOUR PROFILE SERVICE ---
    // This method processes a Thymeleaf template to create the HTML body.
    public void sendEmailWithTemplate(String to, String subject, String templateName, Map<String, Object> templateModel) {
        Context context = new Context();
        context.setVariables(templateModel);
        String htmlContent = templateEngine.process(templateName, context);
        this.sendEmailWithHtmlContent(to, subject, htmlContent);
    }

    // --- PRIVATE HELPER METHOD ---
    // This is the core method that actually calls the Brevo API.
    private void sendEmailWithHtmlContent(String to, String subject, String htmlContent) {
        WebClient webClient = webClientBuilder
                .baseUrl("https://api.brevo.com/v3/smtp/email")
                .defaultHeader("accept", "application/json")
                .defaultHeader("api-key", brevoApiKey)
                .defaultHeader("content-type", "application/json")
                .build();

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("email", fromEmail, "name", fromName));
        payload.put("to", new Object[]{Map.of("email", to)});
        payload.put("subject", subject);
        payload.put("htmlContent", htmlContent);

        webClient.post()
                .body(Mono.just(payload), Map.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("✅ Email sent: " + response))
                .doOnError(error -> System.err.println("❌ Email send failed: " + error.getMessage()))
                .block();
    }
}