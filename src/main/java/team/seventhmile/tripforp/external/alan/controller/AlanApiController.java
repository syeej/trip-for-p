package team.seventhmile.tripforp.external.alan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.seventhmile.tripforp.external.alan.dto.AlanApiResponse;
import team.seventhmile.tripforp.external.alan.service.AlanApiService;

@RestController
@RequestMapping("/api/alan")
@RequiredArgsConstructor
public class AlanApiController {

    private final AlanApiService alanApiService;

    @GetMapping
    public AlanApiResponse process(
        @RequestParam(name = "content") String content,
        @RequestParam(name = "client_id") String clientId,
        @RequestParam(name = "start_date", required = false) String startDate,
        @RequestParam(name = "end_date", required = false) String endDate,
        @RequestParam(name = "interests", required = false) String interests,
        @RequestParam(name = "budget", required = false) String budget
    ) {
        return alanApiService.processAlanApiRequest(content, clientId, startDate, endDate, interests, budget);
    }
}
