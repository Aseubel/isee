package com.aseubel.isee.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Aseubel
 * @date 2025/4/16 上午12:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryImageRequest implements Serializable {

    private String farm;

    private String area;

}
