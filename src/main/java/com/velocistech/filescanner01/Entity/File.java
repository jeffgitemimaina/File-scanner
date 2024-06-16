package com.velocistech.filescanner01.Entity;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
public class File implements Serializable{
    @Id
//    @SequenceGenerator(
//            name = "file_sequence",
//            sequenceName = "file_sequence",
//            allocationSize = 1
//    )
//
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "file_sequence"
//    )
    private Long id;

    private String  taskid;

    private String file_name;

    private String query_status;

    private String md5_hash;

    private String sha256_hash;

    private String sha1_hash;

    private String sha3_384_hash;

    private Integer file_size;

    private String first_seen;

    private String mime_type;

    private String last_seen;

    private String clamav_results;

    private  String static_results;

    private  String unpack_results;

    private String email;
//    @ManyToOne
//    @JoinColumn(
//            nullable = false,
//            name = "user_auth_id"
//    )
//    private UserAuth appUser;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String Task_id) {
        this.taskid =Task_id;
    }

    public String getFilename() {
        return file_name;
    }

    public void setFilename(String file_name) {
        this.file_name = file_name;
    }

    public String getQuery_status() {
        return query_status;
    }

    public void setQuery_status(String querystatus) {
        this.query_status = querystatus;
    }

    public String getMd5_hash() {
        return md5_hash;
    }

    public void setMd5_hash(String md5_hash) {
        this.md5_hash = md5_hash;
    }

    public String getSha256_hash() {
        return sha256_hash;
    }

    public void setSha256_hash(String sha256_hash) {
        this.sha256_hash = sha256_hash;
    }

    public String getSha1_hash() {
        return sha1_hash;
    }

    public void setSha1_hash(String sha1_hash) {
        this.sha1_hash = sha1_hash;
    }

    public String getSha3_384_hash() {
        return sha3_384_hash;
    }

    public void setSha3_384_hash(String sha3_384_hash) {
        this.sha3_384_hash = sha3_384_hash;
    }

    public Integer getFile_size() {
        return file_size;
    }

    public void setFile_size(Integer file_size) {
        this.file_size = file_size;
    }

    public String getFirst_seen() {
        return first_seen;
    }

    public void setFirst_seen(String first_seen) {
        this.first_seen = first_seen;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(String last_seen) {
        this.last_seen = last_seen;
    }

    public String getClamav_results() {
        return clamav_results;
    }

    public void setClamav_results(String clamav_results) {
        this.clamav_results = clamav_results;
    }

    public String getStatic_results() {
        return static_results;
    }

    public void setStatic_results(String static_results) {
        this.static_results = static_results;
    }

    public String getUnpack_results() {
        return unpack_results;
    }

    public void setUnpack_results(String unpack_results) {
        this.unpack_results = unpack_results;
    }

}

