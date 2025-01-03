package io.mosip.admin.bulkdataupload.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author anusha
 *
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "schema_def", schema = "master")
public class SchemaDefinition extends BaseEntity {
	
	@Id
	@Column(name = "id", nullable = false)
	private String id;
	
	@Column(name = "def_name", nullable = false)
	private String defName;
	
	@Column(name = "def_type", nullable = false)
	private String defType;
	
	@Column(name = "add_props", nullable = false)
	private boolean additionalProperties;
	
	@Column(name = "def_json", nullable = false, length=2000)
	private String definitionJson;
}
