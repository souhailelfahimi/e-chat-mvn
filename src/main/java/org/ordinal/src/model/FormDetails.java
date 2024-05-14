package org.ordinal.src.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormDetails {
   private String name;
   private String ip;
   private int port;
   private boolean isClient;



}
