package com.iwfc.model.user;

/**
 * Represents an IWFC gym member / client who browses schedules and books sessions.
 */
public class Member extends User {

    private String membershipTier;

    public Member(String id, String name, String email, String membershipTier) {
        super(id, name, email, UserRole.MEMBER);
        this.membershipTier = membershipTier != null ? membershipTier.trim() : "Standard";
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier != null ? membershipTier.trim() : "Standard";
    }

    @Override
    public String getDashboardSummary() {
        return String.format("Member Profile: %s (%s) | Tier: %s | Permissions: Slot Viewing & Booking",
                getName(), getId(), membershipTier);
    }
}
