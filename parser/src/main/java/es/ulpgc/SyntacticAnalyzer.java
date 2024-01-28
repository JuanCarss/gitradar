package es.ulpgc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface SyntacticAnalyzer {
    List<Token> analyze(Stream<String> lines);

    static SyntacticAnalyzer create(final LexicalAnalyzer lexicalAnalyzer) {
        return lines -> lines
                    .map(lexicalAnalyzer::analyze)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
    }
