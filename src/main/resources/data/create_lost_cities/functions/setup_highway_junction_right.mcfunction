gamerule commandBlockOutput false

execute if block ~1 ~ ~1 create:track run place template create_lost_cities:track_y_going_e_se_from_w ~ ~ ~
execute if block ~-1 ~ ~ create:track if block ~ ~ ~-1 create:track run place template create_lost_cities:track_y_going_n_ne_from_s ~-5 ~ ~
execute if block ~-1 ~ ~ create:track if block ~ ~ ~1 create:track run place template create_lost_cities:track_y_going_w_nw_from_e ~ ~ ~1
execute if block ~-1 ~ ~ create:track if block ~-1 ~ ~1 create:track run place template create_lost_cities:track_y_going_s_sw_from_n ~-4 ~ ~

clone ~ ~1 ~ ~ ~1 ~ ~ ~ ~