package io.project.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailParams {

    private String id;
    private String emailTo;
}
