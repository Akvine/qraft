package ru.akvine.qraft.rest.dto.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NextPage {
    private int page;

    private int count;
}
