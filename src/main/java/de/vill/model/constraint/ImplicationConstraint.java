package de.vill.model.constraint;

import de.vill.model.building.VariableReference;

import java.util.*;

import static de.vill.util.Util.isJustAnd;

public class ImplicationConstraint extends Constraint {
    private Constraint left;
    private Constraint right;

    public ImplicationConstraint(Constraint left, Constraint right) {
        this.left = left;
        this.right = right;
    }

    public Constraint getLeft() {
        return left;
    }

    public Constraint getRight() {
        return right;
    }

    @Override
    public String toString(boolean withSubmodels, String currentAlias) {
        StringBuilder result = new StringBuilder();
        result.append(left.toString(withSubmodels, currentAlias));
        result.append(" => ");
        result.append(right.toString(withSubmodels, currentAlias));
        return result.toString();
    }

    @Override
    public List<Constraint> getConstraintSubParts() {
        return Arrays.asList(left, right);
    }

    @Override
    public void replaceConstraintSubPart(Constraint oldSubConstraint, Constraint newSubConstraint) {
        if (left == oldSubConstraint) {
            left = newSubConstraint;
        } else if (right == oldSubConstraint) {
            right = newSubConstraint;
        }
    }

    @Override
    public Constraint clone() {
        return new ImplicationConstraint(left.clone(), right.clone());
    }

    @Override
    public int hashCode(int level) {
        final int prime = 31;
        int result = prime * level + (left == null ? 0 : left.hashCode(1 + level));
        result = prime * result + (right == null ? 0 : right.hashCode(1 + level));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ImplicationConstraint other = (ImplicationConstraint) obj;
        return Objects.equals(left, other.left) && Objects.equals(right, other.right);
    }

    @Override
    public List<VariableReference> getReferences() {
        List<VariableReference> references = new ArrayList<>();
        references.addAll(left.getReferences());
        references.addAll(right.getReferences());
        return references;
    }

    public int extractTseitinSubConstraints(Map<Integer, Constraint> substitutionMapping, int n) {
        int a1 = left.extractTseitinSubConstraints(substitutionMapping, n);
        int a2 = right.extractTseitinSubConstraints(substitutionMapping, a1+1);
        int finalA = a1;
        Constraint l1 = new LiteralConstraint(new VariableReference() {
            @Override
            public String getIdentifier() {
                return "x_" + finalA;
            }
        });
        int finalA1 = a2;
        Constraint l2 = new LiteralConstraint(new VariableReference() {
            @Override
            public String getIdentifier() {
                return "x_" + finalA1;
            }
        });
        if(a1 == 0) {
            l1 = left;
        }else{
            n = a1 + 1;
        }
        if(a2 == 0) {
            l2 = right;
        }else{
            n = a2 + 1;
        }

        Constraint newConstraint = new ImplicationConstraint(l1, l2);
        substitutionMapping.put(n, newConstraint);
        return n;
    };
}
