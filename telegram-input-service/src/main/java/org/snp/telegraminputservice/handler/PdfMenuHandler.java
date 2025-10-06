package org.snp.telegraminputservice.handler;

import org.snp.telegraminputservice.common.RequestResult;
import org.snp.telegraminputservice.dto.PressureRecordDtoRs;
import org.snp.telegraminputservice.dto.ReportDtoRq;
import org.snp.telegraminputservice.formatter.FileNameFormatter;
import org.snp.telegraminputservice.mapper.ReportMapper;
import org.snp.telegraminputservice.messages.MessagesProperties;
import org.snp.telegraminputservice.model.UserSession;
import org.snp.telegraminputservice.model.UserState;
import org.snp.telegraminputservice.service.HealthDataClient;
import org.snp.telegraminputservice.service.PdfClient;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
public class PdfMenuHandler extends AbstractMenuHandler {

    private final HealthDataClient healthDataClient;
    private final PdfClient pdfClient;
    private final ReportMapper reportMapper;
    private final FileNameFormatter fileNameFormatter;

    public PdfMenuHandler(MessagesProperties messagesProperties,
                          HealthDataClient healthDataClient,
                          PdfClient pdfClient,
                          ReportMapper reportMapper,
                          FileNameFormatter fileNameFormatter) {
        super(messagesProperties);
        this.healthDataClient = healthDataClient;
        this.pdfClient = pdfClient;
        this.reportMapper = reportMapper;
        this.fileNameFormatter = fileNameFormatter;
    }

    @Override
    protected UserState targetState() {
        return UserState.PDF_MENU;
    }

    @Override
    protected List<String> commands() {
        return List.of("За последние 7 дней",
                "За последние 14 дней",
                "За последние 30 дней",
                "Назад",
                "Главное меню"
        );
    }

    public List<PartialBotApiMethod<?>> handle(Message message, UserSession userSession) {
        long chatId = message.getChatId();
        String text = message.getText();
        long userId = message.getFrom().getId();

        SendMessage menuMessage = createMessage(message);
        SendMessage errorMessage = createMessage(message);
        SendMessage resultMessage = createMessage(message);

        switch (text) {
            case "За последние 7 дней" -> {
                return pdfRequest(7, userId, chatId, menuMessage, errorMessage,
                        userSession, message);
            }
            case "За последние 14 дней" -> {
                return pdfRequest(14, userId, chatId, menuMessage, errorMessage,
                        userSession, message);
            }
            case "За последние 30 дней" -> {
                return pdfRequest(30, userId, chatId, menuMessage, errorMessage,
                        userSession, message);
            }
            case "Назад" -> goToReceiveMenu(userSession, menuMessage);
            case "Главное меню" -> goToMainMenu(userSession, menuMessage);
            default -> resultMessage.setText(messagesProperties.getPdfMenu().getUnknownRequest());
        }
        return multiple(errorMessage, resultMessage, menuMessage);
    }

    private List<PartialBotApiMethod<?>> underDevelopment(SendMessage resultMessage) {
        resultMessage.setText("в разработке");
        return single(resultMessage);
    }

    private List<PartialBotApiMethod<?>> pdfRequest(int days, long userId, long chatId,
                                                    SendMessage menuMessage, SendMessage errorMessage,
                                                    UserSession userSession, Message message) {

        RequestResult<List<PressureRecordDtoRs>> result = healthDataClient.getByIdAndDays(userId, days);
        if (!result.isSuccess()) {
            errorMessage.setText(messagesProperties.getPdfMenu().getFailedToReceiveRecords());
            goToMainMenu(userSession, menuMessage);
            return multiple(errorMessage, menuMessage);
        }
        List<PressureRecordDtoRs> recordDtoRs = result.data();
        if (recordDtoRs == null || recordDtoRs.isEmpty()) {
            errorMessage.setText(messagesProperties.getPdfMenu().getNoRecordsForPeriod());
            goToMainMenu(userSession, menuMessage);
            return multiple(errorMessage, menuMessage);
        }
        ReportDtoRq reportDtoRq = reportMapper.buildReportDtoRq(message, userId, recordDtoRs, days);
        byte[] pdf = pdfClient.generateReport(reportDtoRq);
        SendDocument pdfFile = new SendDocument();
        pdfFile.setChatId(chatId);
        pdfFile.setDocument(new InputFile(new ByteArrayInputStream(pdf), fileNameFormatter.fileNameGenerator()));
        goToMainMenu(userSession, menuMessage);
        return multiple(pdfFile, menuMessage);
    }
}