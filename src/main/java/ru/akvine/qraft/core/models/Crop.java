package ru.akvine.qraft.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Crop {
    private LineSegment lineSegment;
    private List<List<Crop>> lineSegments;

    public Crop() {}
}
