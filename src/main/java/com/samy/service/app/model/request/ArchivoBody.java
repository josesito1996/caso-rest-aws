package com.samy.service.app.model.request;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@ToString
@JsonIgnoreProperties
public class ArchivoBody implements Serializable {

    private static final long serialVersionUID = -7294533497589989422L;

    @NotNull
    @NotEmpty
    @Schema(title = "Nombre archivo", example = "WhatsApp Image 2021-10-18 at 08.35.31.jpeg")
    private String nombreArchivo;

    @JsonInclude(Include.NON_NULL)
    @Schema(title = "Base 64", example = "dGVzdCBpbnB1dA==")
    private String base64;

    @NotNull
    @NotEmpty
    @JsonInclude(Include.NON_NULL)
    @Schema(title = "tipo", example = "image/jpeg", description = "Es el MIME del archivo : text/plain\r\n"
            + "text/html\r\n" + "image/jpeg\r\n" + "image/png\r\n" + "audio/mpeg\r\n"
            + "audio/ogg\r\n" + "audio/*\r\n" + "video/mp4")
    private String tipo;

    @JsonProperty("id_archivo")
    @Schema(title = "Id archivo", example = "f1e273e0-e0ac-42b1-8a9c-2b72da805aa6")
    private String idArchivo;

    @JsonInclude(Include.NON_NULL)
    @Schema(title = "Estado", example = "false", description = "Identifica si el archivo esta o no activo en la BD")
    private boolean estado;

    @NotNull
    @NotEmpty
    @Schema(title = "Tamaño", example = "212 kB", description = "Pedo del archivo en KB")
    private String tamaño;
    
    private String subidoPor;
}
