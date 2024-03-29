﻿using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;

namespace DataLinkLayer.Models
{
    public class Profile
    {
        public int ID { get; set; }
        public string fireBaseID { get; set; }
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string NickName { get; set; }
        public long DateOfBirth { get; set; }
        public string base64 { get; set; }
        public long? DateCreated { get; set; }
        [JsonIgnore]
        public byte[] ProfilePicture { get; set; }
    }
}
