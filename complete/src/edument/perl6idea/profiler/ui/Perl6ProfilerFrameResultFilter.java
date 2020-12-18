package edument.perl6idea.profiler.ui;

public enum Perl6ProfilerFrameResultFilter {
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
