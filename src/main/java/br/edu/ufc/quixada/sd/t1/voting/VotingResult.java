package br.edu.ufc.quixada.sd.t1.voting;

public class VotingResult {
    private String candidateId;
    private String candidateName;
    private long votes;
    private double percentage;
    private boolean winner;

    public VotingResult() {
    }

    public VotingResult(String candidateId, String candidateName, long votes, double percentage, boolean winner) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.votes = votes;
        this.percentage = percentage;
        this.winner = winner;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}