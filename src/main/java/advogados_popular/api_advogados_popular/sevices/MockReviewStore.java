package advogados_popular.api_advogados_popular.sevices;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockReviewStore {
    public record Review(long id, String author, int rating, String comment, long createdAt) {}

    private final Map<Long, List<Review>> byLawyer = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public List<Review> list(long lawyerId) {
        return byLawyer.getOrDefault(lawyerId, new ArrayList<>());
    }

    public Review add(long lawyerId, String author, int rating, String comment) {
        Review r = new Review(seq.getAndIncrement(), author, rating, comment, Instant.now().toEpochMilli());
        byLawyer.computeIfAbsent(lawyerId, k -> new ArrayList<>()).add(0, r);
        return r;
    }

    public double average(long lawyerId) {
        List<Review> list = list(lawyerId);
        if (list.isEmpty()) return 0.0;
        return Math.round(list.stream().mapToInt(Review::rating).average().orElse(0) * 10.0) / 10.0;
    }
}
