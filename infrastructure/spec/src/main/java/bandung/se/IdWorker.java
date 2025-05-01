package bandung.se;

import lombok.Getter;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public interface IdWorker {

    default long getId() {
        return getId("deadbeef");
    }

    long getId(String useragent);

    IdWorker STANDALONE = new Snowflake(0L, 0L, 0L, 0L, 0L);

    /**
     * @see  com.twitter.service.snowflake.IdWorker
     */
    class Snowflake implements IdWorker {

        private void genCounter(String agent) {
//            Stats.incr("ids_generated");
//            Stats.incr("ids_generated_%s".format(agent));
        }

        //private Counter exceptionCounter = Stats.getCounter("exceptions")

        private final Logger log = Logger.getLogger(Snowflake.class.getName());

//        private final Random rand = new Random();

        final long twepoch = 1745559060000L; // my daughter birthday

        @Getter private final long workerId;

        @Getter private final long datacenterId;

        private long sequence;

        private final long workerIdShift;

        private final long datacenterIdShift;

        private final long timestampLeftShift;

        private final long sequenceMask;

        private long lastTimestamp;

        public Snowflake(long workerId, long datacenterId) {
            this(workerId, datacenterId, 0L, 5L, 5L);
        }

        public Snowflake(long workerId, long datacenterId, /* Reporter reporter, */long sequence) {
            this(workerId, datacenterId, sequence, 5L, 5L);
        }

        Snowflake(long workerId, long datacenterId, long sequence, long workerIdBits, long datacenterIdBits) {
            long maxWorkerId = -1L ^ (-1L << workerIdBits);
            // sanity check for workerId
            if (workerId > maxWorkerId || workerId < 0) {
                // exceptionCounter.incr(1)
                throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            this.workerId = workerId;

            long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
            if (datacenterId > maxDatacenterId || datacenterId < 0) {
                // exceptionCounter.incr(1)
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
            }
            this.datacenterId = datacenterId;
            this.sequence = sequence;

            long sequenceBits = 12L;
            workerIdShift = sequenceBits;
            datacenterIdShift = sequenceBits + workerIdBits;
            timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
            sequenceMask = -1L ^ (-1L << sequenceBits);
            lastTimestamp = -1L;
            log.log(Level.INFO, "worker starting. timestamp left shift {0}, datacenter id bits {1}, worker id bits {2}, sequence bits {3}, workerid {4}",
                    new Object[] {timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId});
        }

        @Override
        public long getId(String useragent) {
            if (!validUseragent(useragent)) {
                // exceptionCounter.incr(1);
                throw new IllegalArgumentException(); // InvalidUserAgentError
            }
            long id = nextId();
            genCounter(useragent);
            // reporter.report(new AuditLogEntry(id, useragent, rand.nextLong));
            return id;
        }

        protected synchronized long nextId() {
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                // exceptionCounter.incr(1)
                log.log(Level.SEVERE, "clock is moving backwards.  Rejecting requests until {0}.", lastTimestamp);
                throw new IllegalStateException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp)); // InvalidSystemClock
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0;
            }
            lastTimestamp = timestamp;
            return ((timestamp - twepoch) << timestampLeftShift) |
                    (datacenterId << datacenterIdShift) |
                    (workerId << workerIdShift) |
                    sequence;
        }

        protected long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        protected long timeGen() {
            return System.currentTimeMillis();
        }

        boolean validUseragent(String useragent) {
            return agentParser.matcher(useragent).matches();
        }

        private final Pattern agentParser = Pattern.compile("[a-zA-Z][a-zA-Z\\-0-9]*");
    }

}
