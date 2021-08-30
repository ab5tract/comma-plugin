package edument.perl6idea.profiler;

import edument.perl6idea.profiler.model.Perl6ProfileData;

import java.util.Deque;

public interface Perl6ProfileDataManager {
    Deque<Perl6ProfileData> getProfileResults();
    void saveProfileResult(Perl6ProfileData data);
    void removeProfileResult(Perl6ProfileData data);
}
