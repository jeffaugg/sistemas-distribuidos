package br.edu.ufc.quixada.sd.t1.voting;

import java.time.Instant;

public class Vote {
    private String voterUsername;
    private String candidateId;
    private Instant timestamp;

    public Vote() {
    }

    public Vote(String voterUsername, String candidateId, Instant timestamp) {
        this.voterUsername = voterUsername;
        this.candidateId = candidateId;
        this.timestamp = timestamp;
    }

    public String getVoterUsername() {
        return voterUsername;
    }

    public void setVoterUsername(String voterUsername) {
        this.voterUsername = voterUsername;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}