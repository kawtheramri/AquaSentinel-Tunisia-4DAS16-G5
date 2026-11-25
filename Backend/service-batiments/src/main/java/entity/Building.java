package entity;

import entity.enums.BuildingType;
import entity.enums.BuildingStatus;
import jakarta.persistence.*;
import entity.enums.MeterStatus;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BuildingType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BuildingStatus status = BuildingStatus.TO_ASSIGN;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(name = "surface_m2")
    private Double surfaceM2;

    @Column(name = "occupants_count")
    private Integer occupantsCount;

    @Column(name = "tariff_category", length = 50)
    private String tariffCategory;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meter> meters = new ArrayList<>();

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagerAssignment> managerAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BaselineProfile> baselineProfiles = new ArrayList<>();

    public Building() {
    }

    // --- Méthodes métier du diagramme ---

    public void activate() {
        this.status = BuildingStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = BuildingStatus.INACTIVE;
    }

    public void assignManager(ManagerAssignment assignment) {
        assignment.setBuilding(this);
        this.managerAssignments.add(assignment);
        if (this.status == BuildingStatus.TO_ASSIGN) {
            this.status = BuildingStatus.ACTIVE;
        }
    }

    public void replaceMeter(Meter oldMeter, Meter newMeter) {
        if (oldMeter != null) {
            oldMeter.remove();
        }
        if (newMeter != null) {
            newMeter.setBuilding(this);
            newMeter.install();
            this.meters.add(newMeter);
        }
    }

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BuildingType getType() {
        return type;
    }

    public void setType(BuildingType type) {
        this.type = type;
    }

    public BuildingStatus getStatus() {
        return status;
    }

    public void setStatus(BuildingStatus status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getSurfaceM2() {
        return surfaceM2;
    }

    public void setSurfaceM2(Double surfaceM2) {
        this.surfaceM2 = surfaceM2;
    }

    public Integer getOccupantsCount() {
        return occupantsCount;
    }

    public void setOccupantsCount(Integer occupantsCount) {
        this.occupantsCount = occupantsCount;
    }

    public String getTariffCategory() {
        return tariffCategory;
    }

    public void setTariffCategory(String tariffCategory) {
        this.tariffCategory = tariffCategory;
    }

    public List<Meter> getMeters() {
        return meters;
    }

    public void setMeters(List<Meter> meters) {
        this.meters = meters;
    }

    public List<ManagerAssignment> getManagerAssignments() {
        return managerAssignments;
    }

    public void setManagerAssignments(List<ManagerAssignment> managerAssignments) {
        this.managerAssignments = managerAssignments;
    }

    public List<BaselineProfile> getBaselineProfiles() {
        return baselineProfiles;
    }

    public void setBaselineProfiles(List<BaselineProfile> baselineProfiles) {
        this.baselineProfiles = baselineProfiles;
    }
}