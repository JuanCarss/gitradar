package es.ulpgc;

import es.ulpgc.metrics.Metrics;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static es.ulpgc.metrics.ClassMetrics.*;
import static es.ulpgc.metrics.MethodMetrics.*;
import static es.ulpgc.metrics.Metrics.*;
import static java.lang.Character.isUpperCase;

public class MetricsGenerator {
    private final MetricsBuilder METRICS_BUILDER = new MetricsBuilder();
    private final ClassMetricsBuilder CLASS_METRICS_BUILDER = new ClassMetricsBuilder();
    private final MethodMetricsBuilder METHOD_METRICS_BUILDER = new MethodMetricsBuilder();

    public Metrics generate(List<Token> tokens) {
        List<Integer> classesIndexes = getClassesIndexes(tokens);
        addLastTokensIndex(classesIndexes, tokens);
        for (int i = 0; i < classesIndexes.size() - 1 ; i++) {
            analyzeClass(getTokens(tokens, classesIndexes.get(i), classesIndexes.get(i + 1)));
            METRICS_BUILDER.addClass(CLASS_METRICS_BUILDER.build());
        }
        return METRICS_BUILDER.build();
    }

    private static List<Token> getTokens(List<Token> tokens, int start, int end) {
        return tokens.subList(start, end);
    }

    private void analyzeClass(List<Token> classTokens) {
        List<Integer> methodsStarts = getMethodsStarts(classTokens);
        addLastTokensIndex(methodsStarts, classTokens);
        addClassFirstlines(getTokensBeforeMethods(classTokens, methodsStarts));
        for (int i = 0; i < methodsStarts.size() -1 ; i++) {
            analyzeMethod(getTokens(classTokens, methodsStarts.get(i), methodsStarts.get(i + 1)));
            CLASS_METRICS_BUILDER.addMethod(METHOD_METRICS_BUILDER.build());
        }
    }

    private static List<Token> getTokensBeforeMethods(List<Token> classTokens, List<Integer> methodsStarts) {
        return classTokens.subList(0, methodsStarts.get(0));
    }

    private void addClassFirstlines(List<Token> tokens) {
        tokens.parallelStream()
                .filter(MetricsGenerator::isLineBreak)
                .forEach(token -> CLASS_METRICS_BUILDER.addNonMethodLine());
    }

    private void analyzeMethod(List<Token> methodTokens) {
        countMethodCodeLines(methodTokens);
        countMethodCyclomaticComplexity(methodTokens);
    }

    private void countMethodCyclomaticComplexity(List<Token> methodTokens) {
        methodTokens.parallelStream()
                .filter(MetricsGenerator::isControlStructure)
                .forEach(token  -> METHOD_METRICS_BUILDER.increaseCyclomaticComplexity());
    }

    private static boolean isControlStructure(Token token) {
        return token.type().equals(Token.Type.CONTROL_STRUCTURE);
    }

    private void countMethodCodeLines(List<Token> methodTokens) {
        methodTokens.parallelStream()
                .filter(MetricsGenerator::isLineBreak)
                .forEach(token  -> METHOD_METRICS_BUILDER.increaseLines());
    }

    private static boolean isLineBreak(Token token) {
        return token.type().equals(Token.Type.LINE_BREAK);
    }

    private void addLastTokensIndex(List<Integer> indexList, List<Token> tokens) {
        indexList.add(tokens.size());
    }

    private List<Integer> getClassesIndexes(List<Token> tokens) {
        return IntStream.range(0, tokens.size())
                .parallel()
                .filter(i -> isClassIdentifier(tokens.get(i)))
                .mapToObj(tokens::get)
                .distinct()
                .mapToInt(tokens::indexOf)
                .boxed()
                .collect(Collectors.toList());
    }

    private static boolean isClassIdentifier(Token token) {
        return isIdentifier(token) && startsWithUpperCase(token);
    }

    private static boolean isIdentifier(Token token) {
        return token.type().equals(Token.Type.IDENTIFIER);
    }

    private static boolean startsWithUpperCase(Token token) {
        return isUpperCase(token.text().charAt(0));
    }

    private List<Integer> getMethodsStarts(List<Token> classTokens) {
        return IntStream.range(0, classTokens.size())
                .parallel()
                .skip(1)
                .filter(i -> isIdentifier(classTokens.get(i)))
                .sorted()
                .boxed()
                .collect(Collectors.toList());
    }
}