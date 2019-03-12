package com.yevhenii.kpi.parallel.computing.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yevhenii.kpi.parallel.computing.models.Data;
import com.yevhenii.kpi.parallel.computing.models.ResultData;
import com.yevhenii.kpi.parallel.computing.statistics.StatsHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class JsonUtils {
    private static final Gson gson = new Gson();
    private static final String INPUT_FILE = "input.json";
    private static final String OUTPUT_FILE = "output.json";
    private static final String STATISTICS_FILE = "statistics.json";

    public static Optional<String> writeInputData(Data data) {
        return write(data, INPUT_FILE);
    }

    public static Optional<String> writeOutputData(ResultData data) {
        return write(data, OUTPUT_FILE);
    }

    public static Optional<Data> readInputData() {
        try {
            byte[] json = Files.readAllBytes(Paths.get(INPUT_FILE));
            Data data = gson.fromJson(new String(json), Data.class);
            return Optional.of(data);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<ResultData> readOutputData() {
        try {
            byte[] json = Files.readAllBytes(Paths.get(OUTPUT_FILE));
            ResultData data = gson.fromJson(new String(json), ResultData.class);
            return Optional.of(data);
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<String> writeStatistics(List<StatsHolder> statistics) {
        return write(statistics, STATISTICS_FILE);
    }

    private static <T> Optional<String> write(T subject, String file) {
        String json = gson.toJson(subject);
        try {
            Files.write(Paths.get(file), json.getBytes());
            return Optional.of(json);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
