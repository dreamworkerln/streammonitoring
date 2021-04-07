package ru.kvanttelecom.tv.streammonitoring.utils.functions;

@FunctionalInterface
public interface TriFunction<A,B,C,R> {
    R apply(A a, B b, C c);
}
