package com.umeng.dplus.leveldb.jline;

import jline.console.completer.Completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static jline.internal.Preconditions.checkNotNull;

/**
 * Completer which contains multiple completers and aggregates them together.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.3
 */
public class AggregateCompleter implements Completer {
    private final List<Completer> completers = new ArrayList<Completer>();

    public AggregateCompleter() {
    }

    /**
     * Construct an AggregateCompleter with the given collection of completers.
     * The completers will be used in the iteration order of the collection.
     *
     * @param completers the collection of completers
     */
    public AggregateCompleter(final Collection<Completer> completers) {
        checkNotNull(completers);
        this.completers.addAll(completers);
    }

    /**
     * Construct an AggregateCompleter with the given completers.
     * The completers will be used in the order given.
     *
     * @param completers the completers
     */
    public AggregateCompleter(final Completer... completers) {
        this(Arrays.asList(completers));
    }

    public void addCompleter(Completer completer) {
        completers.add(completer);
    }

    public void removeCompleter(Completer completer) {
        completers.remove(completer);
    }

    /**
     * Retrieve the collection of completers currently being aggregated.
     *
     * @return the aggregated completers
     */
    public Collection<Completer> getCompleters() {
        return completers;
    }

    /**
     * Perform a completion operation across all aggregated completers.
     *
     * @return the highest completion return value from all completers
     * @see Completer#complete(String, int, java.util.List)
     */
    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates) {
        // buffer could be null
        checkNotNull(candidates);

        List<Completion> completions = new ArrayList<Completion>(completers.size());

        // Run each completer, saving its completion results
        int max = -1;
        for (Completer completer : completers) {
            Completion completion = new Completion(candidates);
            completion.complete(completer, buffer, cursor);

            // Compute the max cursor position
            max = Math.max(max, completion.cursor);

            completions.add(completion);
        }

        // Append candidates from completions which have the same cursor position as max
        for (Completion completion : completions) {
            if (completion.cursor == max) {
                for (CharSequence cs : completion.candidates) {
                    if (!candidates.contains(cs)) {
                        candidates.add(cs);
                    }
                }
            }
        }

        return max;
    }

    /**
     * @return a string representing the aggregated completers
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{completers=" + completers + "}";
    }

    private class Completion {
        final List<CharSequence> candidates;

        int cursor;

        Completion(final List<CharSequence> candidates) {
            checkNotNull(candidates);
            this.candidates = new LinkedList<CharSequence>(candidates);
        }

        void complete(final Completer completer, final String buffer, final int cursor) {
            checkNotNull(completer);
            this.cursor = completer.complete(buffer, cursor, candidates);
        }
    }
}
