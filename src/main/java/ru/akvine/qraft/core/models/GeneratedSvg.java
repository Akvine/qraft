package ru.akvine.qraft.core.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GeneratedSvg {
    List<String> paths;
    private int qrSize;
    private int matrixSize;
    private double pointSize;
}
