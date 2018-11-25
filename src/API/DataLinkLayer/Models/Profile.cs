﻿using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Text;
using static System.Net.Mime.MediaTypeNames;

namespace DataLinkLayer.Models
{
    public class Profile
    {
        public int ID { get; set; }

        public int fireBaseID { get; set; }

        public string FirstName { get; set; }

        public string LastName { get; set; }

        public string NickName { get; set; }

        public int Age { get; set; }

        public string Country { get; set; }


        public string base64 { get; set; }

        public DateTime DateCreated { get; set; }

        public Pilgrimage[] CompletedPilgrimages { get; set; }

        [JsonIgnore]
        public byte[] ProfilePicture { get; set; }


    }
}