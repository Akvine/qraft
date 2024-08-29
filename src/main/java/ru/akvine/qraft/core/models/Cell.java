package ru.akvine.qraft.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Cell {
    private int x;
    private int y;
    private String blockId;
    private boolean cornerBlock;
    private byte painted;
}
