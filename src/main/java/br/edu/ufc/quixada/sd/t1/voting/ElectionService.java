package br.edu.ufc.quixada.sd.t1.voting;

import br.edu.ufc.quixada.sd.t1.protocol.UserRole;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ElectionService {
    private final ConcurrentMap<String, Candidate> candidates = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Vote> votesByVoter = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, VotingSession> sessions = new ConcurrentHashMap<>();
    private final Instant votingDeadline;
    private volatile boolean manuallyClosed;

    public ElectionService(Duration votingDuration) {
        this.votingDeadline = Instant.now().plus(votingDuration);
    }

    public synchronized VotingSession login(String username, UserRole role) {
        String token = UUID.randomUUID().toString();
        VotingSession session = new VotingSession(token, username, role, Instant.now());
        sessions.put(token, session);
        return session;
    }

    public synchronized void addCandidate(VotingSession session, Candidate candidate) {
        ensureAdmin(session);
        ensureVotingOpen();
        candidates.put(candidate.getId(), candidate);
    }

    public synchronized void removeCandidate(VotingSession session, String candidateId) {
        ensureAdmin(session);
        ensureVotingOpen();
        candidates.remove(candidateId);
    }

    public List<Candidate> listCandidates(VotingSession session) {
        ensureLogged(session);
        return new ArrayList<>(candidates.values());
    }

    public synchronized Vote vote(VotingSession session, String candidateId) {
        ensureLogged(session);
        ensureVoter(session);
        ensureVotingOpen();

        Candidate candidate = candidates.get(candidateId);
        if (candidate == null) {
            throw new IllegalArgumentException("Candidato inexistente.");
        }

        Vote vote = new Vote(session.getUsername(), candidateId, Instant.now());
        votesByVoter.put(session.getUsername(), vote);
        return vote;
    }

    public synchronized List<VotingResult> calculateResults() {
        Map<String, Long> countByCandidate = votesByVoter.values().stream()
                .collect(Collectors.groupingBy(Vote::getCandidateId, LinkedHashMap::new, Collectors.counting()));

        long totalVotes = Math.max(1L, votesByVoter.size());
        long maxVotes = countByCandidate.values().stream().mapToLong(Long::longValue).max().orElse(0L);

        List<VotingResult> results = new ArrayList<>();
        for (Candidate candidate : candidates.values()) {
            long votes = countByCandidate.getOrDefault(candidate.getId(), 0L);
            double percentage = (votes * 100.0) / totalVotes;
            results.add(new VotingResult(candidate.getId(), candidate.getName(), votes, percentage, votes == maxVotes && maxVotes > 0));
        }
        return results;
    }

    public synchronized boolean isVotingOpen() {
        return !manuallyClosed && Instant.now().isBefore(votingDeadline);
    }

    public synchronized void closeVoting() {
        manuallyClosed = true;
    }

    public Instant getVotingDeadline() {
        return votingDeadline;
    }

    public Map<String, Object> buildStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("deadline", votingDeadline.toString());
        status.put("open", isVotingOpen());
        status.put("candidates", candidates.size());
        status.put("votes", votesByVoter.size());
        return status;
    }

    public VotingSession findSession(String token) {
        return sessions.get(token);
    }

    private void ensureLogged(VotingSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Sessão inválida.");
        }
    }

    private void ensureAdmin(VotingSession session) {
        ensureLogged(session);
        if (session.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Ação permitida apenas para administrador.");
        }
    }

    private void ensureVoter(VotingSession session) {
        if (session.getRole() != UserRole.VOTER) {
            throw new IllegalArgumentException("Ação permitida apenas para eleitor.");
        }
    }

    private void ensureVotingOpen() {
        if (!isVotingOpen()) {
            throw new IllegalStateException("Votação encerrada.");
        }
    }
}