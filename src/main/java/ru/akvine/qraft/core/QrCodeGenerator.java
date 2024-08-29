package ru.akvine.qraft.core;

import lombok.Builder;
import ru.akvine.qraft.core.lib.Mode;
import ru.akvine.qraft.core.lib.QRCode;
import ru.akvine.qraft.core.lib.QrConstants;
import ru.akvine.qraft.core.models.*;
import ru.akvine.qraft.exceptions.URLInvalidException;

import java.util.*;
import java.util.stream.Collectors;

import static ru.akvine.qraft.core.lib.QrConstants.*;

@Builder
public class QrCodeGenerator {
    private boolean cornerBlocksAsCircles;
    private boolean roundInnerCorners;
    private boolean roundOuterCorners;
    private double cornerBlockRadiusFactor;
    private int radiusFactor;
    private int qrSize;
    private String errorCorrectionLevel;

    public List<String> generate(String url) {
        int matrixSize;
        double pointSize;
        GeneratedSvg generatedSvg = new GeneratedSvg();

        QRCode code = new QRCode(errorCorrectionLevel);
        code.setErrorCorrectionLevel(errorCorrectionLevel);
        QRCode.set8BitByteEncoding("UTF-8");
        code.setTypeNumber(generateTypeNumber(url, errorCorrectionLevel));
        code.addData(url, Mode.MODE_8BIT_BYTE);
        code.make();

        Cell[][] matrix = convertToCells(code.getModules());

        matrixSize = matrix.length;
        pointSize = qrSize * 1.0 / matrixSize;

        generatedSvg.setMatrixSize(matrixSize);
        generatedSvg.setQrSize(qrSize);
        generatedSvg.setPointSize(pointSize);

        Cell[][] detectedBlocks = detectBlocks(matrix, generatedSvg);
        resetId();

        Map<String, Map<String, Crop>> lines = detectLines(detectedBlocks, generatedSvg);
        return detectPaths(lines, generatedSvg);
    }

    private Cell[][] convertToCells(Boolean[][] qrCodeMatrix) {
        int height = qrCodeMatrix.length;
        int width = qrCodeMatrix[0].length;

        Cell[][] matrix = new Cell[height][width];

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (qrCodeMatrix[y][x]) {
                    matrix[y][x] = new Cell(x, y, "none", false, (byte) 1);
                } else {
                    matrix[y][x] = new Cell(x, y, "none", false, (byte) 0);
                }
            }
        }

        return matrix;
    }

    protected Cell[][] detectBlocks(Cell[][] matrix, GeneratedSvg generatedSvg) {
        int matrixSize = generatedSvg.getMatrixSize();

        for (int y = 0; y < matrixSize; ++y) {
            for (int x = 0; x < matrixSize; ++x) {
                Cell currentCell = matrix[y][x];

                if (currentCell.getBlockId().equals("none") && currentCell.getPainted() == 1) {
                    List<Cell> cells = new ArrayList<>();
                    String blockId = generateId();
                    List<Cell> findedCells = findNeighbors(matrix, currentCell, (byte) 1, cells);

                    for (Cell cell : findedCells) {
                        int cellX = cell.getX();
                        int cellY = cell.getY();
                        matrix[cellY][cellX].setBlockId(blockId);

                        if ((cellX < 8 && cellY < 8) || (cellX > matrixSize - 8 && cellY < 8) || (cellX < 8 && cellY > matrixSize - 8)) {
                            cell.setCornerBlock(true);
                        }
                    }
                }
            }
        }

        return matrix;
    }

    protected List<Cell> findNeighbors(Cell[][] matrix, Cell cell, byte painted, List<Cell> cells) {
        cells.add(cell);

        for (int[] offset : neighborOffsets) {
            int neighborX = cell.getX() + offset[0];
            int neighborY = cell.getY() + offset[1];

            Optional<Cell> value = cells.stream().filter(obj -> obj.getX() == neighborX && obj.getY() == neighborY).findFirst();
            if (value.isEmpty()) {
                Cell neighborCell = getProp(matrix, new int[]{neighborY, neighborX});

                if (neighborCell != null && neighborCell.getPainted() == painted) {
                    cells = findNeighbors(matrix, neighborCell, neighborCell.getPainted(), cells);
                }
            }
        }

        return cells;
    }

    private Map<String, Map<String, Crop>> detectLines(Cell[][] detectedBlocks, GeneratedSvg generatedSvg) {
        double pointSize = generatedSvg.getPointSize();
        int matrixSize = generatedSvg.getMatrixSize();

        double pathRadius = (pointSize / 2) * Math.min(radiusFactor, 10);
        double cornerBlockPathRadius = (pointSize / 2) * Math.min(cornerBlockRadiusFactor, 10);

        Map<String, Map<String, Crop>> detectedLines = new LinkedHashMap<>();

        for (int y = 0; y < matrixSize; y++) {
            for (int x = 0; x < matrixSize; x++) {
                Cell currentCell = detectedBlocks[y][x];
                if (currentCell.getBlockId().equals("none")) {
                    continue;
                }

                if (currentCell.isCornerBlock() && cornerBlocksAsCircles) {
                    continue;
                }

                int i = 0;
                for (int[] offset : neighborOffsets) {
                    Cell neighborCell = getProp(detectedBlocks, new int[]{y + offset[0], x + offset[1]});
                    if (neighborCell == null || !neighborCell.getBlockId().equals(currentCell.getBlockId())) {
                        if (currentCell.getBlockId() != null) {
                            int lastIndex = 0;
                            Map<String, Crop> linesSegmentsWithCrops;

                            if (detectedLines.get(currentCell.getBlockId()) == null) {
                                linesSegmentsWithCrops = new LinkedHashMap<>();
                            } else {
                                linesSegmentsWithCrops = detectedLines.get(currentCell.getBlockId());
                                lastIndex = getLastIndex(linesSegmentsWithCrops);
                                lastIndex++;
                            }

                            int p1X = x + contours[i][0][1];
                            int p1Y = y + contours[i][0][0];
                            int p2X = x + contours[i][1][1];
                            int p2Y = y + contours[i][1][0];

                            Point point1 = new Point(p1X, p1Y);
                            Point point2 = new Point(p2X, p2Y);
                            double radius = currentCell.isCornerBlock() ? cornerBlockPathRadius : pathRadius;

                            LineSegment lineSegment = new LineSegment(false, currentCell, point1, point2, radius);
                            Crop crop = new Crop().setLineSegment(lineSegment);
                            linesSegmentsWithCrops.put(String.valueOf(lastIndex), crop);

                            detectedLines.put(currentCell.getBlockId(), linesSegmentsWithCrops);
                        }
                    }
                    i++;
                }
            }
        }

        for (String blockId : detectedLines.keySet()) {
            Map<String, Crop> line = detectedLines.get(blockId);
            line.get(QrConstants.FIRST_ELEMENT).getLineSegment().setProcessed(true);

            Map<String, Crop> result = new LinkedHashMap<>();
            result.put(QrConstants.FIRST_ELEMENT, line.get(QrConstants.FIRST_ELEMENT));
            proc(line.get(QrConstants.FIRST_ELEMENT).getLineSegment().getPoint2().getY(), line.get(QrConstants.FIRST_ELEMENT)
                    .getLineSegment().getPoint2().getX(), result, line.get(QrConstants.FIRST_ELEMENT)
                    .getLineSegment().getCell(), line, 1);
            detectedLines.replace(blockId, result);

            Crop crop = new Crop();
            crop.setLineSegment(new LineSegment());
            crop.setLineSegments(new ArrayList<>());
            detectedLines.get(blockId).put("crops", crop);

            boolean checkCrops = true;

            while (checkCrops) {
                Crop notProcessedLineSegment;
                try {
                    notProcessedLineSegment = line.values().stream().filter(obj -> !obj.getLineSegment().isProcessed()).findFirst().get();
                } catch (Exception e) {
                    checkCrops = false;
                    continue;
                }

                notProcessedLineSegment.getLineSegment().setProcessed(true);
                Map<String, Crop> cropResult = new LinkedHashMap<>();
                cropResult.put(QrConstants.FIRST_ELEMENT, notProcessedLineSegment);
                proc(notProcessedLineSegment.getLineSegment().getPoint2().getY(), notProcessedLineSegment.getLineSegment().getPoint2().getX(), cropResult,
                        notProcessedLineSegment.getLineSegment().getCell(), line, 1);

                List<Crop> cropResultList = new ArrayList<>();

                for (Map.Entry<String, Crop> entry : cropResult.entrySet()) {
                    cropResultList.add(entry.getValue());
                }

                Collections.reverse(cropResultList);

                for (Crop lineSegmentWithCrops : cropResultList) {
                    Point op2 = lineSegmentWithCrops.getLineSegment().getPoint2();
                    lineSegmentWithCrops.getLineSegment().setPoint2(lineSegmentWithCrops.getLineSegment().getPoint1());
                    lineSegmentWithCrops.getLineSegment().setPoint1(op2);
                }
                detectedLines.get(blockId).get("crops").getLineSegments().add(cropResultList);
            }
        }

        return detectedLines;
    }

    private void proc(long py, long px, Map<String, Crop> result, Cell oCell, Map<String, Crop> line, int count) {
        List<Crop> nextSegs = new ArrayList<>(line.values());
        List<Crop> sortedSegs = nextSegs.stream().filter(seg -> {
            if (!seg.getLineSegment().isProcessed()) {
                return (seg.getLineSegment().getPoint1().getY() == py && seg.getLineSegment().getPoint1().getX() == px) ||
                        (seg.getLineSegment().getPoint2().getY() == py && seg.getLineSegment().getPoint2().getX() == px);
            }
            return false;
        }).sorted((a, b) -> {
            if (a.getLineSegment().getCell() == oCell) {
                return -1;
            }
            return 1;
        }).collect(Collectors.toList());


        if (!sortedSegs.isEmpty()) {
            Crop nextSeg = sortedSegs.get(0);
            nextSeg.getLineSegment().setProcessed(true);
            LineSegment resultSeg = null;

            if (nextSeg.getLineSegment().getPoint1().getY() == py && nextSeg.getLineSegment().getPoint1().getX() == px) {
                resultSeg = new LineSegment(nextSeg.getLineSegment().getPoint1(), nextSeg.getLineSegment().getPoint2(), nextSeg.getLineSegment().getCornerBlockPathRadius());
            } else if (nextSeg.getLineSegment().getPoint2().getY() == py && nextSeg.getLineSegment().getPoint2().getX() == px) {
                resultSeg = new LineSegment(nextSeg.getLineSegment().getPoint2(), nextSeg.getLineSegment().getPoint1(), nextSeg.getLineSegment().getCornerBlockPathRadius());
            }
            Crop crop = new Crop();
            crop.setLineSegment(resultSeg);

            result.put(String.valueOf(count), crop);
            ++count;
            proc(resultSeg.getPoint2().getY(), resultSeg.getPoint2().getX(), result, nextSeg.getLineSegment().getCell(), line, count);
        }
    }

    protected List<String> detectPaths(Map<String, Map<String, Crop>> lines, GeneratedSvg generatedSvg) {
        int qrSize = generatedSvg.getQrSize();
        int matrixSize = generatedSvg.getMatrixSize();
        double pointSize = generatedSvg.getPointSize();

        List<String> paths = new ArrayList<>();
        paths.add(generateStartSvg(qrSize));

        for (String blockId : lines.keySet()) {
            String path = "";
            List<List<Crop>> crops = extractCrops(lines.get(blockId));

            int lineIdx = 0;
            for (List<Crop> line : crops) {
                int segIdx = 0;
                for (Crop crop : line) {
                    LineSegment seg = crop.getLineSegment();
                    double x = seg.getPoint1().getX();
                    double y = seg.getPoint2().getY();
                    double cr = seg.getCornerBlockPathRadius();

                    x *= pointSize;
                    y *= pointSize;

                    double xpcr = round(x + cr);
                    double ypcr = round(y + cr);

                    x = round(x);
                    y = round(y);

                    LineSegment prevSeg, nextSeg;

                    try {
                        prevSeg = line.get((segIdx - 1)).getLineSegment();
                    } catch (Exception e) {
                        prevSeg = line.get((line.size() - 1)).getLineSegment();
                    }
                    try {
                        nextSeg = line.get((segIdx + 1)).getLineSegment();
                    } catch (Exception e) {
                        nextSeg = line.get(0).getLineSegment();
                    }

                    String segDir = getDir(seg);
                    String prevSegDir = getDir(prevSeg);

                    if (segIdx == 0) {
                        if (roundOuterCorners) {
                            if (lineIdx == 0) {
                                path += " M" + xpcr + " " + y + " ";
                            } else {
                                path += " M" + x + " " + ypcr + " ";
                            }
                        } else {
                            path += " M" + x + " " + y + " ";
                        }
                    } else if (segIdx == line.size() - 1) {
                        path += getSubPath(seg, prevSeg, roundOuterCorners, roundInnerCorners, pointSize);
                        path += getSubPath(nextSeg, seg, roundOuterCorners, roundInnerCorners, pointSize);
                        path += "Z";
                    } else if (!prevSegDir.equals(segDir)) {
                        path += getSubPath(seg, prevSeg, roundOuterCorners, roundInnerCorners, pointSize);
                    }
                    segIdx++;
                }
                lineIdx++;
            }
            String value = "<path d=\"" + path + "\"/>";
            paths.add(value);
        }

        if (cornerBlocksAsCircles) {
            double offsetSize = pointSize * matrixSize - pointSize * 7;

            double[][] offsets = {
                    {0, 0},
                    {offsetSize, 0},
                    {0, offsetSize}
            };

            for (double[] offset : offsets) {
                double ox = offset[0];
                double oy = offset[1];

                double centerX = round((pointSize * 7) / 2 + ox);
                double centerY = round((pointSize * 7) / 2 + oy);

                double outerRadius = round((pointSize * 7) / 2);
                double innerRadius = round((pointSize * 7) / 2 - pointSize);

                paths.add(drawBigCircle(centerX, centerY, outerRadius, innerRadius));
                paths.add(drawSmallCircle(centerX, centerY, outerRadius, pointSize, innerRadius));
            }
        }

        paths.add(generateEndSvg());
        return paths;
    }

    private int generateTypeNumber(String url, String ecl) {
        int length = url.length() + 3;

        int type = 1;
        int limit = 0;
        int len = QrConstants.QR_CODE_LIMIT_LENGTH.length;

        for (int i = 0; i <= len; i++) {
            int[] table = {0};
            try {
                table = QrConstants.QR_CODE_LIMIT_LENGTH[i];
            } catch (Exception exception) {
                throw new URLInvalidException("URL is too long, ex=" + exception.getMessage());
            }

            if (ecl.equals("L")) {
                limit = table[0];
            } else if (ecl.equals("M")) {
                limit = table[1];
            } else if (ecl.equals("Q")) {
                limit = table[2];
            } else if (ecl.equals("H")) {
                limit = table[3];
            } else {
                throw new RuntimeException("Unknwon error correction level: " + ecl);
            }

            if (length <= limit) {
                break;
            }

            type++;
        }

        return type;
    }

    private String getSubPath(LineSegment seg, LineSegment prevSeg, boolean roundOuterCorners,
                              boolean roundInnerCorners, double pointSize) {
        double x = seg.getPoint1().getX();
        double y = seg.getPoint1().getY();
        double cr = seg.getCornerBlockPathRadius();

        x *= pointSize;
        y *= pointSize;

        double xmcr = round(x - cr);
        double xpcr = round(x + cr);

        double ymcr = round(y - cr);
        double ypcr = round(y + cr);

        x = round(x);
        y = round(y);

        String segDir = getDir(seg);
        String prevSegDir = getDir(prevSeg);

        String path = "";
        StringBuilder builder = new StringBuilder();
        if (cr > 0 && roundOuterCorners && prevSegDir.equals("we") && segDir.equals("ns")) {
            builder
                    .append("L").append(xmcr).append(" ")
                    .append(y).append(" ").append("Q")
                    .append(x).append(" ").append(y)
                    .append(" ").append(x).append(" ").append(ypcr);
        } else if (cr > 0 && roundOuterCorners && prevSegDir.equals("ns") && segDir.equals("ew")) {
            builder
                    .append("L").append(x).append(" ")
                    .append(ymcr).append(" ").append("Q")
                    .append(x).append(" ").append(y).append(" ")
                    .append(xmcr).append(" ").append(y);
        } else if (cr > 0 && roundOuterCorners && prevSegDir.equals("ew") && segDir.equals("sn")) {
            builder
                    .append("L").append(xpcr).append(" ")
                    .append(y).append(" ").append("Q")
                    .append(x).append(" ").append(y)
                    .append(" ").append(x).append(" ")
                    .append(ymcr);
        } else if (cr > 0 && roundOuterCorners && prevSegDir.equals("sn") && segDir.equals("we")) {
            builder
                    .append("L").append(x).append(" ")
                    .append(ypcr).append(" ").append("Q")
                    .append(x).append(" ").append(y).append(" ")
                    .append(xpcr).append(" ").append(y);
        } else if (cr > 0 && roundInnerCorners && prevSegDir.equals("sn") && segDir.equals("ew")) {
            builder
                    .append("L").append(x).append(" ")
                    .append(ypcr).append(" ").append("Q")
                    .append(x).append(" ").append(y).append(" ")
                    .append(xmcr).append(" ").append(y);
        } else if (cr > 0 && roundInnerCorners && prevSegDir.equals("ew") && segDir.equals("ns")) {
            builder
                    .append("L").append(xpcr).append(" ")
                    .append(y).append(" ").append("Q").append(x)
                    .append(" ").append(y).append(" ")
                    .append(x).append(" ").append(ypcr);
        } else if (cr > 0 && roundInnerCorners && prevSegDir.equals("ns") && segDir.equals("we")) {
            builder
                    .append("L").append(x).append(" ")
                    .append(ymcr).append(" ").append("Q")
                    .append(x).append(" ").append(y).append(" ")
                    .append(xpcr).append(" ").append(y);
        } else if (cr > 0 && roundInnerCorners && prevSegDir.equals("we") && segDir.equals("sn")) {
            builder
                    .append("L").append(xmcr).append(" ")
                    .append(y).append(" ").append("Q")
                    .append(x).append(" ").append(y)
                    .append(" ").append(x).append(" ")
                    .append(ymcr);
        } else {
            builder
                    .append("L").append(x).append(" ").append(y)
                    .append(" ");
        }

        path += builder.toString();
        return path;
    }



    private String getDir(LineSegment seg) {
        if (seg.getPoint1().getX() == seg.getPoint2().getX()) {
            if (seg.getPoint1().getY() > seg.getPoint2().getY()) {
                return "sn";
            }
            return "ns";
        }
        if (seg.getPoint1().getY() == seg.getPoint2().getY()) {
            if (seg.getPoint1().getX() > seg.getPoint2().getX()) {
                return "ew";
            }
            return "we";
        }

        return "";
    }

    public static Cell getProp(Cell[][] matrix, int[] coordinates) {
        try {
            return matrix[coordinates[0]][coordinates[1]];
        } catch (Exception e) {
            return null;
        }
    }


    public static int getLastIndex(Map<String, Crop> cropMap) {
        return cropMap.keySet().stream().mapToInt(Integer::parseInt).max().orElse(0);
    }

    public static String generateStartSvg(double size) {
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 " + size + " " + size + "\"" + " width=" + "\"" + size + "\"" + " height=" + "\"" + size + "\"" + " " + "fill=\"#182026\"" + ">";
    }

    public static String generateEndSvg() {
        return "</svg>";
    }

    public static String drawBigCircle(double centerX, double centerY, double outerRadius, double innerRadius) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("<path d=\"").append("M ").append(centerX)
                .append(" ").append((centerY - outerRadius)).append(" ")
                .append("A ").append(outerRadius).append(" ")
                .append(outerRadius).append(" ").append("0 1 0")
                .append(" ").append(centerX).append(" ").append(round(centerY + outerRadius))
                .append("A ").append(outerRadius).append(" ")
                .append(outerRadius).append(" ").append("0 1 0" )
                .append(" ").append(centerX).append(" ")
                .append(round(centerY - outerRadius)).append(" Z ").append("M ")
                .append(centerX).append(" ").append((centerY - innerRadius))
                .append(" ").append("A ").append(innerRadius)
                .append(" ").append(innerRadius).append(" ")
                .append("0 1 1").append(" ").append(centerX)
                .append(" ").append(round(centerY + innerRadius)).append(" ")
                .append("A ").append(innerRadius).append(" ")
                .append(innerRadius).append(" ").append("0 1 1")
                .append(" ").append(centerX).append(" ")
                .append(round(centerY - innerRadius)).append(" Z").append(" \"/>");
        return builder.toString();
    }

    public static String drawSmallCircle(double centerX, double centerY, double outerRadius, double pointSize, double innerRadius) {
        outerRadius = Math.round((pointSize * 7) / 2 - pointSize * 2);
        StringBuilder builder = new StringBuilder();
        builder
                .append("<path d=\"").append("M").append(" ")
                .append(centerX).append(" ").append((centerY - outerRadius))
                .append(" ").append("A ").append(outerRadius)
                .append(" ").append(outerRadius).append(" ")
                .append("0 1 0").append(" ").append(centerX)
                .append(" ").append(round(centerY + outerRadius))
                .append(" ").append("A ").append(outerRadius)
                .append(" ").append(outerRadius).append(" ")
                .append("0 1 0").append(" ").append(centerX)
                .append(" ").append(round(centerY - outerRadius))
                .append(" Z").append(" \"/>");
        return builder.toString();
    }

    public static int calculateResizeCoeff(int size) {
        return (size * 380) / 1465;
    }

    public static List<List<Crop>> extractCrops(Map<String, Crop> line) {
        Set<String> keys = line.keySet();
        List<List<Crop>> lists = new ArrayList<>();
        List<Crop> notCropsList = new ArrayList<>();

        for (String key : keys) {
            if (!key.equals("crops")) {
                notCropsList.add(line.get(key));
            }
        }
        lists.add(notCropsList);

        for (String key : keys) {
            if (key.equals("crops")) {
                lists.addAll(line.get(key).getLineSegments());
            }
        }

        return lists;
    }

    public static double round(double value) {
        return Math.round(value * 1e1) / 1e1;
    }
}
