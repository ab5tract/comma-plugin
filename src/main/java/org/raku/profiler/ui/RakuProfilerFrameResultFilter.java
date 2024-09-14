package org.raku.profiler.ui;

public enum RakuProfilerFrameResultFilter {
    Everything {
        @Override
        public String toString() {
            return "Everywhere";
        }
    }, NoCore {
        @Override
        public String toString() {
            return "Everywhere except CORE";
        }
    }, NoExternals {
        @Override
        public String toString() {
            return "Only this project";
        }
    }
}
