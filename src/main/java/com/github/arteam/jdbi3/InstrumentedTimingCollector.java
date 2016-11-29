package com.github.arteam.jdbi3;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.github.arteam.jdbi3.strategies.SmartNameStrategy;
import com.github.arteam.jdbi3.strategies.StatementNameStrategy;
import org.jdbi.v3.core.StatementContext;
import org.jdbi.v3.core.TimingCollector;

import java.util.concurrent.TimeUnit;

/**
 * A {@link TimingCollector} implementation for JDBI which uses the SQL objects' class names and
 * method names for millisecond-precision timers.
 */
public class InstrumentedTimingCollector implements TimingCollector {

    private final MetricRegistry registry;
    private final StatementNameStrategy statementNameStrategy;

    public InstrumentedTimingCollector(MetricRegistry registry) {
        this(registry, new SmartNameStrategy());
    }

    public InstrumentedTimingCollector(MetricRegistry registry,
                                       StatementNameStrategy statementNameStrategy) {
        this.registry = registry;
        this.statementNameStrategy = statementNameStrategy;
    }

    @Override
    public void collect(long elapsedTime, StatementContext ctx) {
        final Timer timer = getTimer(ctx);
        timer.update(elapsedTime, TimeUnit.NANOSECONDS);
    }

    private Timer getTimer(StatementContext ctx) {
        return registry.timer(statementNameStrategy.getStatementName(ctx));
    }
}
