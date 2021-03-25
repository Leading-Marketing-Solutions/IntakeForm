package com.lmsplus.intakeform.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "field_values")
public class FieldValues extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "form_id", referencedColumnName = "id")
    IntakeForm intakeForm;

    @Column
    String name;

    @Column
    String value;

    public IntakeForm getIntakeForm() {
        return intakeForm;
    }

    public void setIntakeForm(IntakeForm intakeForm) {
        this.intakeForm = intakeForm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
