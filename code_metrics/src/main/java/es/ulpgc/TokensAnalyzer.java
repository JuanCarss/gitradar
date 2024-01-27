package es.ulpgc;

import es.ulpgc.analytics.ClassAnalytics;
import es.ulpgc.analytics.FileAnalytics;
import es.ulpgc.analytics.MethodAnalytics;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Character.isUpperCase;

public class TokensAnalyzer {

    private final FileAnalytics.FileAnalyticsBuilder fileAnalyticsBuilder = new FileAnalytics.FileAnalyticsBuilder();
    private final ClassAnalytics.ClassAnalyticsBuilder classAnalyticsBuilder = new ClassAnalytics.ClassAnalyticsBuilder();
    private final MethodAnalytics.MethodAnalyticsBuilder methodAnalyticsBuilder = new MethodAnalytics.MethodAnalyticsBuilder();

    public FileAnalytics analyze(List<Token> tokens) {
        List<Integer> classStarts = getClassesStarts(tokens);
        addLastTokensIndex(classStarts, tokens);
        for (int i = 0; i < classStarts.size() -1 ; i++) {
            analyzeClass(extractCodePart(tokens, classStarts.get(i), classStarts.get(i + 1)));
            fileAnalyticsBuilder.addClass(classAnalyticsBuilder.build());
        }
        return fileAnalyticsBuilder.build();
    }

    private static List<Token> extractCodePart(List<Token> tokens, int starts, int ends) {
        return tokens.subList(starts, ends);
    }

    private void analyzeClass(List<Token> classTokens) {
        List<Integer> methodsStarts = getMethodsStarts(classTokens);
        addLastTokensIndex(methodsStarts, classTokens);
        addClassFirstlinesToAnalytics(getTokensBeforeMethods(classTokens, methodsStarts));
        for (int i = 0; i < methodsStarts.size() -1 ; i++) {
            analyzeMethod(extractCodePart(classTokens, methodsStarts.get(i), methodsStarts.get(i + 1)));
            classAnalyticsBuilder.addMethod(methodAnalyticsBuilder.build());
        }
    }

    private static List<Token> getTokensBeforeMethods(List<Token> classTokens, List<Integer> methodsStarts) {
        return classTokens.subList(0, methodsStarts.get(0));
    }

    private void addClassFirstlinesToAnalytics(List<Token> tokens) {
        tokens.parallelStream()
                .filter(token -> token.type().equals(Token.Type.LINE_BREAK))
                .forEach(token -> classAnalyticsBuilder.addNonMethodCodeLine());
    }


    private void analyzeMethod(List<Token> methodTokens) {
        countMethodCodeLines(methodTokens);
        countMethodCyclomaticComplexity(methodTokens);
    }

    private void countMethodCyclomaticComplexity(List<Token> methodTokens) {
        methodTokens.parallelStream()
                .filter(token -> token.type().equals(Token.Type.CONTROL_STRUCTURE))
                .forEach(token  -> methodAnalyticsBuilder.addCyclomaticComplexity());
    }

    private void countMethodCodeLines(List<Token> methodTokens) {
        methodTokens.parallelStream()
                .filter(token -> token.type().equals(Token.Type.LINE_BREAK))
                .forEach(token  -> methodAnalyticsBuilder.addMethodCodeLine());
    }

    private void addLastTokensIndex(List<Integer> indexList, List<Token> tokens) {
        indexList.add(tokens.size());
    }

    private List<Integer> getClassesStarts(List<Token> tokens) {
        return IntStream.range(0, tokens.size())
                .parallel()
                .filter(i -> tokens.get(i).type().equals(Token.Type.IDENTIFIER))
                .filter(i -> isUpperCase(tokens.get(i).text().charAt(0)))
                .mapToObj(tokens::get)
                .distinct()
                .mapToInt(tokens::indexOf)
                .boxed()
                .collect(Collectors.toList());
    }

    private List<Integer> getMethodsStarts(List<Token> classTokens) {
        return IntStream.range(0, classTokens.size())
                .parallel()
                .skip(1)
                .filter(i -> classTokens.get(i).type().equals(Token.Type.IDENTIFIER))
                .sorted()
                .boxed()
                .collect(Collectors.toList());
    }
}