package com.lmsplus.intakeform.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "field_values")
public class FieldValues extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "form_id", referencedColumnName = "id")
    IntakeForm intakeForm;

    @Column
    String fieldName;

    @Column
    String fieldValue;


}
